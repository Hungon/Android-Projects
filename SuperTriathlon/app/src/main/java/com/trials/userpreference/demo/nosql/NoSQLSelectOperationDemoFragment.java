package com.trials.userpreference.demo.nosql;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.AmazonClientException;
import com.trials.amazonaws.mobile.util.ThreadUtils;
import com.trials.supertriathlon.R;
import com.trials.supertriathlon.SystemManager;
import com.trials.userpreference.demo.DemoFragmentBase;
import com.trials.userpreference.demo.Spinner;

import java.util.List;


public class NoSQLSelectOperationDemoFragment extends DemoFragmentBase implements AdapterView.OnItemClickListener {
    private static final String LOG_TAG = NoSQLSelectOperationDemoFragment.class.getSimpleName();

    /** Bundle key for retrieving the table name from the fragment's arguments. */
    public static final String BUNDLE_ARGS_TABLE_TITLE = "tableTitle";

    /** The NoSQL Table demo operations will be run against. */
    private DemoNoSQLTableBase tableFactory;

    /** The List View containing the NoSQL Operations that may be selected */
    private ListView operationsListView;

    /** The Adapter for the NoSQL Operations List. */
    private ArrayAdapter<DemoNoSQLOperationListItem> operationsListAdapter;

    /** The Application context. */
    private Context appContext;

    /** The Runnable posted to show the spinner. */
    private Spinner spinnerRunner;

    private static String sAttributeNameSelected;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        appContext = getActivity().getApplicationContext();
        final Bundle args = getArguments();
        final String tableName = args.getString(BUNDLE_ARGS_TABLE_TITLE);
        tableFactory = DemoNoSQLTableFactory.instance(getContext().getApplicationContext())
            .getNoSQLTableByTableName(tableName);

        // Inflate the layout for this fragment.
        final View fragmentView = inflater.inflate(
            R.layout.fragment_demo_nosql_select_operation, container, false);

        ((AppCompatActivity) getActivity())
            .getSupportActionBar()
            .setTitle(String.format(appContext.getString(
                R.string.main_fragment_title_nosql_select_operation), tableFactory.getTableName()));

        return fragmentView;
    }

    public void createOperationsList(final View fragmentView) {
        operationsListView = (ListView) fragmentView.findViewById(R.id.nosql_operation_list);
        operationsListAdapter = new DemoNoSQLOperationListAdapter(getActivity(),
            R.layout.demo_nosql_select_operation_list_item);
        operationsListView.setOnItemClickListener(this);
        operationsListView.setAdapter(operationsListAdapter);
        tableFactory.getSupportedDemoOperations(appContext, new DemoNoSQLTableBase.SupportedDemoOperationsHandler() {
            @Override
            public void onSupportedOperationsReceived(final List<DemoNoSQLOperationListItem> supportedOperations) {
                // Populate the operations list.
                operationsListAdapter.addAll(supportedOperations);
            }
        });
    }

    @Override
    public void onViewCreated(final View fragmentView, final Bundle savedInstanceState) {
        spinnerRunner = new Spinner(getActivity());
        createOperationsList(fragmentView);
    }

    private void handleNoResultsFound() {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Re-enable the operations list view.
                operationsListView.setEnabled(true);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle(R.string.nosql_dialog_title_no_results_text);
                dialogBuilder.setMessage(R.string.nosql_dialog_message_no_results_text);
                dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
                dialogBuilder.show();
            }
        });
    }

    private void showResultsForOperation(final DemoNoSQLOperation operation) {
        // On execution complete, open the NoSQLShowResultsDemoFragment.
        final NoSQLShowResultsDemoFragment resultsDemoFragment = new NoSQLShowResultsDemoFragment();
        resultsDemoFragment.setOperation(operation);

        final FragmentActivity fragmentActivity = getActivity();

        if (fragmentActivity != null) {
            fragmentActivity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, resultsDemoFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

        if (operationsListAdapter.getItem(position).getViewType() == DemoNoSQLOperationListAdapter.ViewType.OPERATION.ordinal()) {
            final DemoNoSQLOperation operation = (DemoNoSQLOperation) operationsListAdapter.getItem(position);
            sAttributeNameSelected = ((DemoNoSQLOperation) operationsListAdapter.getItem(position)).getAttribute();

            showSpinner();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean foundResults = false;
                    try {
                        foundResults = operation.loadItems();
                    } catch (final AmazonClientException ex) {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                operationsListView.setEnabled(true);
                                Log.e(LOG_TAG,
                                    String.format("Failed executing selected DynamoDB table (%s) operation (%s) : %s",
                                        tableFactory.getTableName(), operation.getTitle(), ex.getMessage()), ex);
                                DynamoDBUtils.showErrorDialogForServiceException(getActivity(),
                                    getString(R.string.nosql_dialog_title_failed_operation_text), ex);
                            }
                        });
                        return;
                    } finally {
                        dismissSpinner();
                    }

                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (operation.isScan()) {
                                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                dialogBuilder.setTitle("The Current Rank:"+" "+Integer.toString(operation.getNewRank()));
                                dialogBuilder.setMessage("The Preview Rank:"+" "+Integer.toString(operation.getPreviewRank()));
                                dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
                                dialogBuilder.show();
                            }
                        }
                    });
                    if (!foundResults) {
                        handleNoResultsFound();
                    } else {
                        showResultsForOperation(operation);
                    }
                }
            }).start();
        }
    }

    private void showSpinner() {
        // Disable the operations list until query executes.
        operationsListView.setEnabled(false);
        spinnerRunner.schedule();
    }
    private void dismissSpinner() {
        spinnerRunner.cancelOrDismiss();
    }

    public static String getAttributeNameSelected() { return sAttributeNameSelected; }
}
