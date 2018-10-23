/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.trials.supertriathlon;

import com.trials.userpreference.demo.nosql.HasDynamoDB;

import java.util.Random;

public class Constants implements HasProperties, HasDynamoDB {

    static final Random random = new Random();
    public static int getRandomNum(int max,int min){
        int r = random.nextInt(max)+1;
        r = r % ( max - min + 1 ) + min;
        return r;
    }
    public static String convertTableTypeIntoTableName(HasDynamoDB.DynamoDBTableType tableType) {
        for (int i = 0; i < DYNAMO_TABLE_LIST.length; i++) {
            if (tableType == DYNAMO_TABLE_LIST[i]) return TABLE_NAMES[i];
        }
        return "";
    }
    public static String convertTableTypeIntoStageName(DynamoDBTableType tableType) {
        for (int i = 0; i < DYNAMO_TABLE_LIST.length; i++) {
            if (tableType == DYNAMO_TABLE_LIST[i]) return TABLE_SIMPLE_NAME_LIST[i];
        }
        return "";
    }
}
