package com.trials.userpreference.demo.nosql;

import android.content.Context;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBSaveExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.trials.supertriathlon.HasProperties;
import com.trials.supertriathlon.HasRecords;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DemoNoSQLTableBase implements HasDynamoDB ,HasProperties, HasRecords {
    /**
     * @return the name of the table.
     */
    public abstract String getTableName();

    /**
     * Insert Sample data into the table.
     */
    public abstract void insertData(int userId,String userName,int[] records);

    /**
     * Update Sample data in the table.
     */
    public abstract boolean updateItems(Context context);

    /**
     * Remove Sample data from the table.
     */
    public abstract void removeData(int userId);

    /**
     * Handler interface to retrieve the supported table operations.
     */
    public interface SupportedDemoOperationsHandler {
        /**
         * @param supportedOperations the list of supported table operations.
         */
        void onSupportedOperationsReceived(List<DemoNoSQLOperationListItem> supportedOperations);
    }

    /**
     * Get a list of supported demo operations.
     * @return a list of support get, query, and scan operations.
     */
    public abstract void getSupportedDemoOperations(Context context, SupportedDemoOperationsHandler opsHandler);


    public static DynamoDBScanExpression getDynamoDBScanExpression(String attributeName) {
        final Map<String, String> filterExpressionAttributeNames = new HashMap<>();
        filterExpressionAttributeNames.put("#"+attributeName, attributeName);
        final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
        filterExpressionAttributeValues.put(":Min"+attributeName,
                new AttributeValue().withN("0"));
        return new DynamoDBScanExpression()
                .withFilterExpression("#"+attributeName+" > :Min"+attributeName)
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues);
    }

    public static DynamoDBScanExpression getDynamoDBScanExpression(int userId) {
        // Use an expression names Map to avoid the potential for attribute names
        // colliding with DynamoDB reserved words.
        final Map <String, String> filterExpressionAttributeNames = new HashMap<>();
        filterExpressionAttributeNames.put("#hashAttribute", "UserId");
        final Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":startVal", new AttributeValue().withN(Integer.toString(userId)));
        expressionAttributeValues.put(":endVal", new AttributeValue().withN(Integer.toString(userId)));
        final String hashKeyFilterCondition = "#hashAttribute BETWEEN :startVal AND :endVal";
        return new DynamoDBScanExpression()
                .withFilterExpression(hashKeyFilterCondition)
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues);
    }

    public static DynamoDBSaveExpression getDynamoDBSaveExpression(int userId) {
        // Use an expression names Map to avoid the potential for attribute names
        // colliding with DynamoDB reserved words.
        AttributeValue attributeValue = new AttributeValue("UserId");
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue(attributeValue);
        final Map<String, ExpectedAttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put("#hashAttribute",expectedAttributeValue);
        return new DynamoDBSaveExpression().withExpected(expressionAttributeValues);
    }
}
