//
// Copyright 2016 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.13
//
package com.trials.amazonaws.mobile;

import com.amazonaws.regions.Regions;

/**
 * This class defines constants for the developer's resource
 * identifiers and API keys. This configuration should not
 * be shared or posted to any public source code repository.
 */
public class AWSConfiguration {
    // AWS MobileHub user agent string
    public static final String AWS_MOBILEHUB_USER_AGENT =
        "MobileHub 9f5e2a11-1dc8-43cc-9f3d-3dae86f4285a aws-my-sample-app-android-v0.13";
    // AMAZON COGNITO
    public static final Regions AMAZON_COGNITO_REGION =
      Regions.fromName("us-east-1");
    public static final String  AMAZON_COGNITO_IDENTITY_POOL_ID =
        "us-east-1:b14a88ba-94ce-4a62-b089-2183852de866";
    public static final Regions AMAZON_DYNAMODB_REGION =
       Regions.fromName("us-east-1");
}
