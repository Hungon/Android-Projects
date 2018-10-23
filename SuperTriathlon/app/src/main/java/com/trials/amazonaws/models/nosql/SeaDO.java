package com.trials.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "supertriathlon-mobilehub-1066258632-Sea")

public class SeaDO extends StageBase {
    @DynamoDBHashKey(attributeName = "UserId")
    @DynamoDBAttribute(attributeName = "UserId")
    public int getUserId() {
        return _userId;
    }

    public void setUserId(final int _userId) {
        this._userId = _userId;
    }
    @DynamoDBAttribute(attributeName = "TotalScoreEasy")
    public int getTotalScoreEasy() {
        return _totalScoreEasy;
    }

    public void setTotalScoreEasy(final int _totalScoreEasy) {
        this._totalScoreEasy = _totalScoreEasy;
    }
    @DynamoDBAttribute(attributeName = "TotalScoreHard")
    public int getTotalScoreHard() {
        return _totalScoreHard;
    }

    public void setTotalScoreHard(final int _totalScoreHard) {
        this._totalScoreHard = _totalScoreHard;
    }
    @DynamoDBAttribute(attributeName = "TotalScoreNormal")
    public int getTotalScoreNormal() {
        return _totalScoreNormal;
    }

    public void setTotalScoreNormal(final int _totalScoreNormal) {
        this._totalScoreNormal = _totalScoreNormal;
    }
    @DynamoDBAttribute(attributeName = "UserName")
    public String getUserName() {
        return _userName;
    }

    public void setUserName(final String _userName) {
        this._userName = _userName;
    }

    @DynamoDBAttribute(attributeName = "PreviewRankEasy")
    public int getPreviewRankEasy() { return _previewRankEasy; }

    public void setPreviewRankEasy(int rank) { _previewRankEasy = rank; }

    @DynamoDBAttribute(attributeName = "PreviewRankNormal")
    public int getPreviewRankNormal() { return _previewRankNormal; }

    public void setPreviewRankNormal(int rank) { _previewRankNormal = rank; }

    @DynamoDBAttribute(attributeName = "PreviewRankHard")
    public int getPreviewRankHard() { return _previewRankHard; }

    public void setPreviewRankHard(int rank) { _previewRankHard = rank; }

    @DynamoDBAttribute(attributeName = "LatestRankEasy")
    public int getLatestRankEasy() { return _latestRankEasy; }

    public void setLatestRankEasy(int rank) { _latestRankEasy = rank; }

    @DynamoDBAttribute(attributeName = "LatestRankNormal")
    public int getLatestRankNormal() { return _latestRankNormal; }

    public void setLatestRankNormal(int rank) { _latestRankNormal = rank; }

    @DynamoDBAttribute(attributeName = "LatestRankHard")
    public int getLatestRankHard() { return _latestRankHard; }

    public void setLatestRankHard(int rank) { _latestRankHard = rank; }

}
