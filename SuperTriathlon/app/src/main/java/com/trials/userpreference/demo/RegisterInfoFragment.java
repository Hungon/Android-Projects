package com.trials.userpreference.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.trials.amazonaws.mobile.AWSMobileClient;
import com.trials.amazonaws.mobile.util.ThreadUtils;
import com.trials.amazonaws.models.nosql.AllStagesDO;
import com.trials.amazonaws.models.nosql.OffRoadDO;
import com.trials.supertriathlon.Constants;
import com.trials.supertriathlon.HasProperties;
import com.trials.supertriathlon.HasRecords;
import com.trials.supertriathlon.R;
import com.trials.supertriathlon.Sort;
import com.trials.supertriathlon.SystemManager;
import com.trials.userpreference.MainActivity;
import com.trials.userpreference.demo.nosql.DemoNoSQLTableBase;
import com.trials.userpreference.demo.nosql.DemoNoSQLTableFactory;
import com.trials.userpreference.demo.nosql.DynamoDBUtils;
import com.trials.userpreference.demo.nosql.NoSQLSelectTableDemoFragment;

import static com.trials.userpreference.demo.nosql.HasDynamoDB.TABLE_SIMPLE_NAME_ALLSTAGES;
import static com.trials.userpreference.demo.nosql.HasDynamoDB.TABLE_SIMPLE_NAME_LIST;
import static com.trials.userpreference.demo.nosql.HasDynamoDB.TABLE_SIMPLE_NAME_LIST;
import static com.trials.userpreference.demo.nosql.HasDynamoDB.TABLE_SIMPLE_NAME_OFFROAD;

/**
 * Created by Kohei Moroi on 12/13/2016.
 */

public class RegisterInfoFragment extends DemoFragmentBase implements HasProperties, HasRecords {
    private EditText mEditText;
    private static final String TAG = RegisterInfoFragment.class.getSimpleName();
    private static String sUserName;
    private Context mAppContext;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        MainActivity.setCurrentFragment(RegisterInfoFragment.class.getSimpleName());
        // Inflate the layout for this fragment.
        final View fragmentView = inflater.inflate(
                R.layout.fragment_input_form, container, false);
        this.mAppContext = getActivity().getApplicationContext();
        return fragmentView;
    }
    @Override
    public void onViewCreated(final View fragmentView, final Bundle savedInstanceState) {
        super.onViewCreated(fragmentView, savedInstanceState);
        // text filed
        mEditText = new EditText(this.mAppContext);
        mEditText = (EditText) fragmentView.findViewById(R.id.edit_user_name);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,int start, int count, int after) {
                // Title space intentionally left blank
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        // enter button
        final Button enterButton = (Button) fragmentView.findViewById(R.id.edit_text_enter_button);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sUserName = mEditText.getText().toString();
                if (!sUserName.equals("")) {
                    Log.i(TAG, "Enter clicked. Create new user.");

                    // to insert user's info after decided the id.
                    attemptCreatingData();

                    final AppCompatActivity activity = (AppCompatActivity) getActivity();
                    final DemoConfiguration.DemoFeature demoFeature = DemoConfiguration.getDemoFeatureByName("ranking");
                    // to transition to ranking scene
                    if (activity != null && demoFeature != null) {
                        final Fragment fragment = DemoInstructionFragment.newInstance(demoFeature.name);
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_fragment_container, fragment, demoFeature.name)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();
                        // Set the title for the fragment.
                        activity.getSupportActionBar().setTitle(demoFeature.name);
                    }
                } else {
                    Toast.makeText(mAppContext, "入力フォームが空欄です。", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Enter clicked. Not input.");
                }
            }
        });
        // cancel button
        final Button cancelButton = (Button) fragmentView.findViewById(R.id.edit_text_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Cancel clicked.");
            }
        });
    }

    private void attemptCreatingData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int userId = checkAvailableId();
                    SystemManager.setUserId(userId);
                } catch(NetworkOnMainThreadException nte) {
                    Log.e(RegisterInfoFragment.class.getSimpleName(),nte.getMessage());
                }
                if (0 < SystemManager.getUserId()) {
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            dialogBuilder.setTitle(R.string.nosql_dialog_title_register_id_text);
                                            dialogBuilder.setMessage(R.string.nosql_dialog_message_register_id_text);
                                            dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
                                            dialogBuilder.show();
                                            insertData(SystemManager.getUserId());           // to inset the data
                                            SystemManager.setUserExists(true);               // user is existing right now.
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            SystemManager.setUserId(0);
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(R.string.nosql_dialog_message_confirmation_of_creating_data)
                                    .setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }
                    });
                }
            }
        }).start();
    }
    // to insert each record all stages
    private void insertData(final int userId) {
        // get user's name
        final String userName = RegisterInfoFragment.getUserName();
        // get each the best record from the local storage.
        final SystemManager systemManager = new SystemManager(this.mAppContext);
        final int totalRecords[] = new int[LEVEL_NUMBER_LIST.length];
        int index = 0;
        for (final String stageName:TABLE_SIMPLE_NAME_LIST) {
            final int records[] = new int[LEVEL_NUMBER_LIST.length];
            // when the current index is either stage, to get each the best record.
            if (!stageName.equals(TABLE_SIMPLE_NAME_ALLSTAGES)) {
                for (int i = 0; i < records.length; i++) {
                    records[i] = systemManager.getTheRecords(STAGE_NUMBER_LIST[index],LEVEL_NUMBER_LIST[i])[RECORD_TOTAL];
                    totalRecords[i] += records[i];
                }
            } else { // aggregate score
                System.arraycopy(totalRecords,0,records,0,records.length);
            }
            // instance of table class
            final DemoNoSQLTableBase instance = DemoNoSQLTableFactory.instance(getContext().getApplicationContext())
                    .getNoSQLTableByTableName(stageName);
            index++;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        instance.insertData(userId,userName,records);
                    } catch (final AmazonClientException ex) {
                        // The insertData call already logs the error, so we only need to
                        // show the error dialog to the user at this point.
                        DynamoDBUtils.showErrorDialogForServiceException(getActivity(),
                                getString(R.string.nosql_dialog_title_failed_operation_text), ex);
                        return;
                    }
                    if (stageName.equals(TABLE_SIMPLE_NAME_ALLSTAGES)) {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                dialogBuilder.setTitle(R.string.nosql_dialog_title_added_sample_data_text);
                                dialogBuilder.setMessage(R.string.nosql_dialog_message_added_sample_data_text);
                                dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
                                dialogBuilder.show();
                                // eventually, to save user's info.
                                systemManager.saveUserInfo(userName,userId);
                            }
                        });
                        onDestroy();
                    }
                }
            }).start();
        }
    }

    final int checkAvailableId() {
        // retrieve the table's size and to decide the id by the size.
        // get table in off-road
        DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        PaginatedScanList<AllStagesDO> result = mapper.scan(AllStagesDO.class, scanExpression);
        int count = 0;
        int dummyId = 0;
        if (0<result.size()) {
            int idList[] = new int[result.size()];
            for (AllStagesDO up : result) {
                idList[count] = up.getUserId();
                count++;
            }
            // to sort the list to get the max value which will be user's id.
            Sort.insertionSort(idList);
            dummyId = idList[idList.length - 1] + 1;
        }
        return (dummyId == 0)?1:dummyId;
    }

    public static String getUserName() { return sUserName; }
}
