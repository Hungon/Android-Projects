package com.trials.userpreference.demo.nosql;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amazonaws.AmazonClientException;
import com.trials.amazonaws.mobile.util.ThreadUtils;
import com.trials.supertriathlon.R;
import com.trials.userpreference.demo.DemoFragmentBase;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoSQLShowResultsDemoFragment extends DemoFragmentBase {
    private static final String LOG_TAG = NoSQLShowResultsDemoFragment.class.getSimpleName();

    /** The NoSQL Operation that was performed. */
    private static DemoNoSQLOperation noSQLOperation;

    /** An executor to handle getting more results in the background. */
    private final Executor singleThreadedExecutor = Executors.newSingleThreadExecutor();

    /** A flag indicating all results have been retrieved. */
    private volatile boolean doneRetrievingResults = false;

    /** The list view showing the results. */
    private ListView resultsList;

    private DemoNoSQLResultListAdapter resultsListAdapter;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment.
        final View fragmentView = inflater.inflate(
            R.layout.fragment_demo_nosql_show_results, container, false);

        return fragmentView;
    }

    private void getNextResults() {
        // if there are more results to retrieve.
        if (!doneRetrievingResults) {
            doneRetrievingResults = true;
            // Get next results group in the background.
            singleThreadedExecutor.execute(new Runnable() {
                List<DemoNoSQLResult> results = null;
                @Override
                public void run() {
                    try {
                        results = noSQLOperation.getNextResultGroup();
                    } catch (final AmazonClientException ex) {
                        Log.e(LOG_TAG, "Failed loading additional results.", ex);
                        DynamoDBUtils.showErrorDialogForServiceException(getActivity(),
                            getString(R.string.nosql_dialog_title_failed_loading_more_results), ex);
                    }
                    if (results == null) {
                        return;
                    }
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            doneRetrievingResults = false;
                            resultsListAdapter.addAll(results);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Reset the results in case of screen rotation.
        noSQLOperation.resetResults();

        // get the list
        resultsList = (ListView) view.findViewById(R.id.nosql_show_results_list);
        // create the list adapter
        resultsListAdapter = new DemoNoSQLResultListAdapter(getContext());

        // set the adapter.
        resultsList.setAdapter(resultsListAdapter);

        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                resultsList.showContextMenuForChild(view);
            }
        });

        // set up a listener to load more items when they scroll to the bottom.
        resultsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, final int scrollState) {
            }

            @Override
            public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    getNextResults();
                }
            }
        });

        resultsList.setOnCreateContextMenuListener(this);

        getNextResults();
    }
    public void setOperation(final DemoNoSQLOperation operation) {
        noSQLOperation = operation;
    }
}
