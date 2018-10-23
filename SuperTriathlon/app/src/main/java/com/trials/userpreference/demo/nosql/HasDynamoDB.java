package com.trials.userpreference.demo.nosql;

/**
 * Created by Kohei Moroi on 10/18/2016.
 */

public interface HasDynamoDB {
    enum DynamoDBTableType {TABLE_OFFROAD,TABLE_ROAD,TABLE_SEA,TABLE_ALL_STAGE}
    DynamoDBTableType DYNAMO_TABLE_LIST[] = {
            DynamoDBTableType.TABLE_OFFROAD,DynamoDBTableType.TABLE_ROAD,
            DynamoDBTableType.TABLE_SEA,DynamoDBTableType.TABLE_ALL_STAGE
    };
    String TABLE_NAME_ALLSTAGES = "supertriathlon-mobilehub-1066258632-AllStages";
    String TABLE_NAME_OFFROAD = "supertriathlon-mobilehub-1066258632-OffRoad";
    String TABLE_NAME_ROAD = "supertriathlon-mobilehub-1066258632-Road";
    String TABLE_NAME_SEA = "supertriathlon-mobilehub-1066258632-Sea";
    // Note that spaces are not allowed in the table name
    String TABLE_NAMES[] = {
            TABLE_NAME_OFFROAD,
            TABLE_NAME_ROAD,
            TABLE_NAME_SEA,
            TABLE_NAME_ALLSTAGES
    };
    String TABLE_SIMPLE_NAME_OFFROAD = "OffRoad";
    String TABLE_SIMPLE_NAME_ROAD = "Road";
    String TABLE_SIMPLE_NAME_SEA= "Sea";
    String TABLE_SIMPLE_NAME_ALLSTAGES = "AllStages";
    String TABLE_SIMPLE_NAME_LIST[] = {
            TABLE_SIMPLE_NAME_OFFROAD,
            TABLE_SIMPLE_NAME_ROAD,
            TABLE_SIMPLE_NAME_SEA,
            TABLE_SIMPLE_NAME_ALLSTAGES
    };
    String TABLE_ATTRIBUTE_PARTITION_KEY = "UserId";
    String TABLE_ATTRIBUTE_USER_NAME = "UserName";
    String TABLE_ATTRIBUTE_EASY = "TotalScoreEasy";
    String TABLE_ATTRIBUTE_NORMAL = "TotalScoreNormal";
    String TABLE_ATTRIBUTE_HARD = "TotalScoreHard";
    // Attribute name list of level
    String TABLE_ATTRIBUTE_NAME_LEVEL_LIST[] = {
            TABLE_ATTRIBUTE_EASY,
            TABLE_ATTRIBUTE_NORMAL,
            TABLE_ATTRIBUTE_HARD
    };
}
