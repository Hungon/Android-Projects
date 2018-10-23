package com.trials.userpreference.demo.nosql;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trials.amazonaws.models.nosql.AllStagesDO;
import com.trials.amazonaws.models.nosql.OffRoadDO;
import com.trials.amazonaws.models.nosql.RoadDO;
import com.trials.amazonaws.models.nosql.SeaDO;
import com.trials.amazonaws.models.nosql.StageBase;

import java.util.Set;

public abstract class DemoNoSQLResult implements HasDynamoDB {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private StageBase result;

    DemoNoSQLResult(StageBase result) {
        this.result = result;
    }


    private void setKeyTextViewStyle(final TextView textView) {
        textView.setTextColor(KEY_TEXT_COLOR);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(5), dp(2), dp(5), 0);
        textView.setLayoutParams(layoutParams);
    }

    /**
     * @param dp number of design pixels.
     * @return number of pixels corresponding to the desired design pixels.
     */
    private int dp(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    private void setValueTextViewStyle(final TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(15), 0, dp(15), dp(2));
        textView.setLayoutParams(layoutParams);
    }

    private void setKeyAndValueTextViewStyles(final TextView keyTextView, final TextView valueTextView) {
        setKeyTextViewStyle(keyTextView);
        setValueTextViewStyle(valueTextView);
    }

    private static String bytesToHexString(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02X", bytes[0]));
        for(int index = 1; index < bytes.length; index++) {
            builder.append(String.format(" %02X", bytes[index]));
        }
        return builder.toString();
    }

    private static String byteSetsToHexStrings(Set<byte[]> bytesSet) {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (byte[] bytes : bytesSet) {
            builder.append(String.format("%d: ", ++index));
            builder.append(bytesToHexString(bytes));
            if (index < bytesSet.size()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public View getResultView(Context context, final View convertView, int position, String attributeName) {
        final LinearLayout layout;
        final TextView resultNumberTextView;
        final TextView userIdKeyTextView;
        final TextView userIdValueTextView;
        final TextView userNameKeyTextView;
        final TextView userNameValueTextView;
        final TextView totalScoreKeyTextView;
        final TextView totalScoreValueTextView;
        final TextView previewRankKeyTextView;
        final TextView previewRankValueTextView;
        int userId = result.get_userId();
        if (convertView == null) {
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            // Result number
            resultNumberTextView = new TextView(context);
            resultNumberTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.addView(resultNumberTextView);
            // UserId
            userIdKeyTextView = new TextView(context);
            userIdValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(userIdKeyTextView, userIdValueTextView);
            layout.addView(userIdKeyTextView);
            layout.addView(userIdValueTextView);
            // UserName
            userNameKeyTextView = new TextView(context);
            userNameValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(userNameKeyTextView, userNameValueTextView);
            layout.addView(userNameKeyTextView);
            layout.addView(userNameValueTextView);
            // Score
            totalScoreKeyTextView = new TextView(context);
            totalScoreValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(totalScoreKeyTextView, totalScoreValueTextView);
            layout.addView(totalScoreKeyTextView);
            layout.addView(totalScoreValueTextView);
            // Preview Rank
            previewRankKeyTextView = new TextView(context);
            previewRankValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(previewRankKeyTextView, previewRankValueTextView);
            layout.addView(previewRankKeyTextView);
            layout.addView(previewRankValueTextView);
        } else {
            // Index number that means the latest rank
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);
            // User id
            userIdKeyTextView = (TextView) layout.getChildAt(1);
            userIdValueTextView = (TextView) layout.getChildAt(2);
            // User name
            userNameKeyTextView = (TextView) layout.getChildAt(3);
            userNameValueTextView = (TextView) layout.getChildAt(4);
            // Total score
            totalScoreKeyTextView = (TextView) layout.getChildAt(5);
            totalScoreValueTextView = (TextView) layout.getChildAt(6);
            // Preview rank
            previewRankKeyTextView = (TextView) layout.getChildAt(7);
            previewRankValueTextView = (TextView) layout.getChildAt(8);
        }
        int score = 0;
        int rank = 0;
        // to set score by level selected
        if (attributeName.equals(TABLE_ATTRIBUTE_EASY)) {
            score = result.get_totalScoreEasy();
            rank = result.get_previewRankEasy();
        } else if (attributeName.equals(TABLE_ATTRIBUTE_NORMAL)) {
            score = result.get_totalScoreNormal();
            rank = result.get_previewRankNormal();
        } else if (attributeName.equals(TABLE_ATTRIBUTE_HARD)) {
            score = result.get_totalScoreHard();
            rank = result.get_previewRankHard();
        }
        // when to show up one user's info only, not to set each item
        if (!Integer.toString(userId).equals(attributeName)) {
            resultNumberTextView.setText(String.format("No.%d", +position + 1));
            totalScoreKeyTextView.setText(attributeName);
            totalScoreValueTextView.setText("" + Integer.toString(score));
            previewRankKeyTextView.setText("Preview Rank");
            previewRankValueTextView.setText(Integer.toString(rank));
        }
        userIdKeyTextView.setText("User Id");
        userIdValueTextView.setText("" + userId);
        userNameKeyTextView.setText("User Name");
        userNameValueTextView.setText(result.get_userName());
        return layout;
    }
}
