package com.trials.userpreference.demo.nosql;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.trials.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.trials.amazonaws.models.nosql.OffRoadDO;

import java.util.Set;

public class DemoNoSQLOffRoadResult extends DemoNoSQLResult {
    DemoNoSQLOffRoadResult(final OffRoadDO result) {
        super(result);
    }
}
