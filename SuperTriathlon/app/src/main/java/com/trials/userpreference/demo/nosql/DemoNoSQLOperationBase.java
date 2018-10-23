package com.trials.userpreference.demo.nosql;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.trials.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.trials.supertriathlon.HasProperties;
import com.trials.supertriathlon.HasRecords;
import com.trials.supertriathlon.R;

import java.util.HashMap;
import java.util.Map;

public abstract class DemoNoSQLOperationBase implements DemoNoSQLOperation {
    protected final String title, stageName;
    protected String mAttributeName;
    protected int newRank = 0;
    protected int previewRank = 0;

    DemoNoSQLOperationBase(final String title, final String stageName) {
        this.title = title;
        this.stageName = stageName;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getAttribute() { return stageName; }

    @Override
    public int getViewType() {
        return DemoNoSQLOperationListAdapter.ViewType.OPERATION.ordinal();
    }

    @Override
    public int getNewRank() { return newRank; }

    @Override
    public int getPreviewRank() { return previewRank; }

    private class SelectOpViewHolder {
        private final TextView titleTextView;
        private final TextView stageNameTextView;

        SelectOpViewHolder(final TextView titleTextView, final TextView stageNameTextView) {
            this.titleTextView = titleTextView;
            this.stageNameTextView = stageNameTextView;
        }
    }

    @Override
    public View getView(final LayoutInflater inflater, final View convertView) {
        final RelativeLayout listItemLayout;

        final SelectOpViewHolder selectOpViewHolder;
        if (convertView != null) {
            listItemLayout = (RelativeLayout) convertView;
            selectOpViewHolder = (SelectOpViewHolder) listItemLayout.getTag();
        } else {
            listItemLayout = (RelativeLayout) inflater.inflate(R.layout.demo_nosql_select_operation_list_item, null);
            selectOpViewHolder = new SelectOpViewHolder(
                (TextView) listItemLayout.findViewById(R.id.nosql_query_operation_title),
                (TextView) listItemLayout.findViewById(R.id.nosql_query_operation_stageName));
            listItemLayout.setTag(selectOpViewHolder);
        }

        selectOpViewHolder.titleTextView.setText(title);
        selectOpViewHolder.stageNameTextView.setText(stageName);

        return listItemLayout;
    }

    @Override
    public boolean isScan() {
        return false;
    }
}
