package com.trials.userpreference.demo.nosql;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.trials.amazonaws.mobile.util.ThreadUtils;
import com.trials.supertriathlon.HasProperties;
import com.trials.supertriathlon.HasRecords;
import com.trials.supertriathlon.R;
import com.trials.supertriathlon.SystemManager;
import com.trials.userpreference.SplashActivity;
import com.trials.userpreference.demo.DemoFragmentBase;

import static com.trials.userpreference.demo.nosql.HasDynamoDB.TABLE_SIMPLE_NAME_ALLSTAGES;
import static com.trials.userpreference.demo.nosql.HasDynamoDB.TABLE_SIMPLE_NAME_LIST;

public class NoSQLSelectTableDemoFragment extends DemoFragmentBase implements HasProperties, HasRecords {

    private Context appContext;
    private final class ListItemViewHolder {
        private final TextView tableName;

        ListItemViewHolder(final TextView tableName) {
            this.tableName = tableName;
        }
    }

    private ArrayAdapter<DemoNoSQLTableBase> tablesAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        final View fragmentView = inflater.inflate(
            R.layout.fragment_demo_nosql_select_table, container, false);

        ((AppCompatActivity) getActivity())
            .getSupportActionBar()
            .setTitle(R.string.main_fragment_title_nosql_select_table);
        appContext = getActivity().getApplicationContext();

        return fragmentView;
    }

    public void createTablesList(final View fragmentView) {
        final ListView listView = (ListView) fragmentView.findViewById(R.id.nosql_table_list);

        // Load the table information
        tablesAdapter = new ArrayAdapter<DemoNoSQLTableBase>(getActivity(), R.layout.demo_nosql_select_table_list_item) {
            @Override
            public View getView(final int position, final View convertView,
                                final ViewGroup parent) {
                final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                final View itemView;
                final ListItemViewHolder viewHolder;
                final TextView tableNameTextView;

                if (convertView != null) {
                    itemView = convertView;
                    viewHolder = (ListItemViewHolder) itemView.getTag();
                    tableNameTextView = viewHolder.tableName;
                } else {
                    itemView = layoutInflater.inflate(R.layout.demo_nosql_select_table_list_item, null);
                    tableNameTextView = (TextView) itemView.findViewById(R.id.nosql_table_name);
                    viewHolder = new ListItemViewHolder(tableNameTextView);
                    itemView.setTag(viewHolder);
                }

                final DemoNoSQLTableBase item = getItem(position);
                tableNameTextView.setText(item.getTableName());
                tableNameTextView.setGravity(Gravity.CENTER);
                tableNameTextView.setPadding(0,0,0,10);
                return itemView;
            }
        };
        listView.setAdapter(tablesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final NoSQLSelectOperationDemoFragment fragment = new NoSQLSelectOperationDemoFragment();

                final Bundle args = new Bundle();
                args.putString(NoSQLSelectOperationDemoFragment.BUNDLE_ARGS_TABLE_TITLE,
                    tablesAdapter.getItem(position).getTableName());
                fragment.setArguments(args);

                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });
    }

    @Override
    public void onViewCreated(final View fragmentView, final Bundle savedInstanceState) {
        super.onViewCreated(fragmentView, savedInstanceState);

        createTablesList(fragmentView);

        final DemoNoSQLTableFactory demoNoSQLTableFactory =
            DemoNoSQLTableFactory.instance(getContext().getApplicationContext());

        for (DemoNoSQLTableBase table : demoNoSQLTableFactory.getNoSQLSupportedTables()) {
            tablesAdapter.add(table);
        }
        // remove the info as pressed
        Button removeDataButton = (Button) fragmentView.findViewById(R.id.button_remove_sample_data);
        removeDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                promptToDeleteData();
            }
        });
        tablesAdapter.notifyDataSetChanged();
    }

    // to remove each record all stages
    private void removeData() {
        final int userId = SystemManager.getUserId();
        final SystemManager systemManager = new SystemManager(appContext);
        for (final String stageName:TABLE_SIMPLE_NAME_LIST) {
            // instance of table class
            final DemoNoSQLTableBase instance = DemoNoSQLTableFactory.instance(getContext().getApplicationContext())
                    .getNoSQLTableByTableName(stageName);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        instance.removeData(userId);
                    } catch (final AmazonClientException ex) {
                        // The removeData call already logs the error, so we only need to
                        // show the error dialog to the user at this point.
                        DynamoDBUtils.showErrorDialogForServiceException(getActivity(),
                                getString(R.string.nosql_dialog_title_failed_operation_text), ex);
                        return;
                    }
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Activity activity = getActivity();
                            if (activity != null) {
                                // when all stages have been removed, to show the dialog.
                                if (stageName.equals(TABLE_SIMPLE_NAME_ALLSTAGES)) {
                                    // eventually, to save the info
                                    systemManager.saveUserInfo("", 0);
                                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                                    dialogBuilder.setTitle(R.string.nosql_dialog_title_removed_data_text);
                                    dialogBuilder.setMessage(R.string.nosql_dialog_message_removed_sample_data_text);
                                    dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
                                    dialogBuilder.show();
                                }
                            } else {
                                    // if our activity has left the foreground, alert the user via a toast.
                                    Toast.makeText(appContext,
                                            R.string.nosql_dialog_title_removed_data_text,
                                            Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private void promptToDeleteData() {
        if (SystemManager.getUserExists()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.nosql_dialog_title_remove_confirmation)
                    .setNegativeButton(android.R.string.no, null);
            builder.setMessage(R.string.nosql_dialog_message_remove_confirmation);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    removeData();
                    SystemManager.setUserExists(false);
                }
            });
            builder.show();
        } else {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(R.string.nosql_dialog_title_no_exist_data_text);
            dialogBuilder.setMessage(R.string.nosql_dialog_message_records_have_already_removed);
            dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
            dialogBuilder.show();
        }
    }
}
