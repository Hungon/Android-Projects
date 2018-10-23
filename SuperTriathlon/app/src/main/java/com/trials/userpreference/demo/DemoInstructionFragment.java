package com.trials.userpreference.demo;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.amazonaws.AmazonClientException;
import com.trials.amazonaws.mobile.util.ThreadUtils;
import com.trials.supertriathlon.R;
import com.trials.userpreference.MainActivity;
import com.trials.userpreference.demo.nosql.DemoNoSQLTableBase;
import com.trials.userpreference.demo.nosql.DemoNoSQLTableFactory;

import static com.trials.userpreference.demo.nosql.HasDynamoDB.TABLE_SIMPLE_NAME_ALLSTAGES;
import static com.trials.userpreference.demo.nosql.HasDynamoDB.TABLE_SIMPLE_NAME_LIST;

/**
 * A fragment that shows a brief instruction of demos.
 */
public class DemoInstructionFragment extends DemoFragmentBase {
    private static final String ARGUMENT_DEMO_FEATURE_NAME = "demo_feature_name";
    private static final double maxVisibleDemos = 3.5;
    private Spinner mSpinner;
    private static boolean sUpdateTable;

    public static DemoInstructionFragment newInstance(final String demoFeatureName) {
        DemoInstructionFragment fragment = new DemoInstructionFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_DEMO_FEATURE_NAME, demoFeatureName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        MainActivity.setCurrentFragment(DemoInstructionFragment.class.getSimpleName());
        this.mSpinner = new Spinner(getActivity());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demo_instruction, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Bundle args = getArguments();
        final String demoFeatureName = args.getString(ARGUMENT_DEMO_FEATURE_NAME);
        final DemoConfiguration.DemoFeature demoFeature = DemoConfiguration.getDemoFeatureByName(demoFeatureName);

        // Set the title for the instruction fragment.
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(demoFeature.titleResId);
        }
        final TextView tvOverview = (TextView) view.findViewById(R.id.text_demo_feature_overview);
        tvOverview.setText(demoFeature.overviewResId);
        final TextView tvDescription = (TextView) view.findViewById(
                R.id.text_demo_feature_description);
        if (demoFeature.descriptionResId > 0) {
            tvDescription.setText(demoFeature.descriptionResId);
        } else {
            final TextView tvDescHeading = (TextView) view.findViewById(R.id.text_demo_feature_description_heading);
            tvDescHeading.setVisibility(View.GONE);
            tvDescription.setVisibility(View.GONE);
        }
        final TextView tvPoweredBy = (TextView) view.findViewById(
                R.id.text_demo_feature_powered_by);
        tvPoweredBy.setText(demoFeature.poweredByResId);

        final ArrayAdapter<DemoConfiguration.DemoItem> adapter = new ArrayAdapter<DemoConfiguration.DemoItem>(
                getActivity(), R.layout.list_item_icon_text_with_subtitle) {
            @Override
            public View getView(final int position, final View convertView,
                                final ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = getActivity().getLayoutInflater()
                            .inflate(R.layout.list_item_demo_button_icon_text, parent, false);
                }
                final DemoConfiguration.DemoItem item = getItem(position);
                final ImageView imageView = (ImageView) view.findViewById(R.id.list_item_icon);

                final TextView title = (TextView) view.findViewById(R.id.list_item_title);

                if (item.iconResId != 0) {
                    imageView.setImageResource(item.iconResId);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                    int padding = dpToPixel(10);
                    title.setPadding(padding, padding, padding, padding);
                }
                if (item.buttonTextResId != 0)
                    title.setText(item.buttonTextResId);
                else
                    title.setText(item.buttonText);

                return view;
            }
        };
        adapter.addAll(demoFeature.demos);

        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        if (adapter.getCount() > (int) maxVisibleDemos) {
            final View listItem = adapter.getView(0, null, listView);
            listItem.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    (int) (maxVisibleDemos * listItem.getMeasuredHeight()));
            listView.setLayoutParams(params);
        }

        // to execute the process as pressed the button.
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                final DemoConfiguration.DemoItem item = adapter.getItem(position);
                final AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    final Fragment fragment = Fragment.instantiate(getActivity(), item.fragmentClassName);
                    Bundle arguments  = new Bundle();
                    arguments.putSerializable("tag", item.tag);
                    fragment.setArguments(arguments);
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, fragment, item.fragmentClassName)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    activity.getSupportActionBar().setTitle(item.titleResId);
                }
            }
        });
        listView.setBackgroundColor(Color.WHITE);

        // when user is existing, to update items
        // the condition will change whenever calling onCreate() within SplashActivity class.
        if (sUpdateTable) {
            showSpinner();      // show spinner
            for (final String stageName:TABLE_SIMPLE_NAME_LIST) {
                // instance of table class
                final DemoNoSQLTableBase instance = DemoNoSQLTableFactory.instance(
                        getContext().getApplicationContext())
                        .getNoSQLTableByTableName(stageName);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            instance.updateItems(getActivity());
                        } catch (final AmazonClientException ex) {
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                    dialogBuilder.setTitle(R.string.nosql_dialog_title_failed_update_item_text);
                                    dialogBuilder.setMessage(R.string.nosql_dialog_message_failed_updating_data_text);
                                    dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
                                    dialogBuilder.show();
                                }
                            });
                            return;
                        } finally {
                            dismissSpinner();
                        }
                        // when all tables were updated, to show the dialog.
                        if (stageName.equals(TABLE_SIMPLE_NAME_ALLSTAGES)) {
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                    dialogBuilder.setTitle(R.string.nosql_dialog_title_updated_data_text);
                                    dialogBuilder.setMessage(R.string.nosql_dialog_message_updated_data_text);
                                    dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
                                    dialogBuilder.show();
                                }
                            });
                            sUpdateTable = false;
                            dismissSpinner();
                        } else {
                            sUpdateTable = true;
                        }
                    }
                }).start();
            }
        }
    }

    private int dpToPixel(int sizeInDp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (sizeInDp * scale + 0.5f);
    }

    private void showSpinner() {
        this.mSpinner.schedule();
    }
    private void dismissSpinner() {
        this.mSpinner.cancelOrDismiss();
    }

    public static void setUpdateTable(boolean updated) { sUpdateTable = updated; }
}
