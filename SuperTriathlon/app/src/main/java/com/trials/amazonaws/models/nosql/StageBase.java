package com.trials.amazonaws.models.nosql;

/**
 * Created by Kohei Moroi on 12/21/2016.
 */

public abstract class StageBase {
    protected String _userName;
    protected int _userId;
    protected int _totalScoreEasy;
    protected int _totalScoreHard;
    protected int _totalScoreNormal;
    protected int _previewRankEasy;
    protected int _previewRankHard;
    protected int _previewRankNormal;
    protected int _latestRankEasy;
    protected int _latestRankHard;
    protected int _latestRankNormal;

    public int get_latestRankEasy() {
        return _latestRankEasy;
    }

    public void set_latestRankEasy(int _latestRankEasy) {
        this._latestRankEasy = _latestRankEasy;
    }

    public int get_latestRankHard() {
        return _latestRankHard;
    }

    public void set_latestRankHard(int _latestRankHard) {
        this._latestRankHard = _latestRankHard;
    }

    public int get_latestRankNormal() {
        return _latestRankNormal;
    }

    public void set_latestRankNormal(int _latestRankNormal) {
        this._latestRankNormal = _latestRankNormal;
    }

    public int get_previewRankEasy() {
        return _previewRankEasy;
    }

    public void set_previewRankEasy(int _previewRankEasy) {
        this._previewRankEasy = _previewRankEasy;
    }

    public int get_previewRankHard() {
        return _previewRankHard;
    }

    public void set_previewRankHard(int _previewRankHard) {
        this._previewRankHard = _previewRankHard;
    }

    public int get_previewRankNormal() {
        return _previewRankNormal;
    }

    public void set_previewRankNormal(int _previewRankNormal) {
        this._previewRankNormal = _previewRankNormal;
    }

    public String get_userName() {
        return _userName;
    }
    public void set_userName(String _userName) {
        this._userName = _userName;
    }

    public int get_userId() {
        return _userId;
    }

    public void set_userId(int _userId) {
        this._userId = _userId;
    }

    public int get_totalScoreEasy() {
        return _totalScoreEasy;
    }

    public void set_totalScoreEasy(int _totalScoreEasy) {
        this._totalScoreEasy = _totalScoreEasy;
    }

    public int get_totalScoreNormal() {
        return _totalScoreNormal;
    }

    public void set_totalScoreNormal(int _totalScoreNormal) {
        this._totalScoreNormal = _totalScoreNormal;
    }

    public int get_totalScoreHard() {
        return _totalScoreHard;
    }

    public void set_totalScoreHard(int _totalScoreHard) {
        this._totalScoreHard = _totalScoreHard;
    }

}
