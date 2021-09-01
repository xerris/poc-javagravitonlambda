package com.myorg;

import java.util.HashMap;

import org.json.simple.JSONArray;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.services.apigateway.Method;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;

public class JavaGravitonLambdaStack extends Stack {
    public JavaGravitonLambdaStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public JavaGravitonLambdaStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

        // Environment variable to separate the environments
        String environment = "dev";

        //Lambda Environment Variables to pass to the Lambdas
        HashMap<String, String> env = new HashMap <String, String>();
        env.put("REGION", "us-east-1");
        env.put("ENVIRONMENT", environment);

        
        //Lambda setup
        Function simpleLambdaFunction = Function.Builder.create(this, "simpleLambda")
                .runtime(Runtime.JAVA_11)
                .functionName("simpleLambda")
                .timeout(Duration.seconds(50))
                .memorySize(512)
                .environment(env)
                .code(Code.fromAsset("target/java_graviton_lambda-0.1.jar"))
                .handler("com.myorg.lambda.SimpleLambdaHandler::handleRequest")
                .build();

        Function gravitonSimpleLambdaFunction = Function.Builder.create(this, "gravitonSimpleLambda")
                .runtime(Runtime.JAVA_11)
                .functionName("gravitonSimpleLambda")
                .timeout(Duration.seconds(50))
                .memorySize(512)
                .environment(env)
                .code(Code.fromAsset("target/java_graviton_lambda-0.1.jar"))
                .handler("com.myorg.lambda.SimpleLambdaHandler::handleRequest")
                .build();

        //Let us configure the Lambdas to be Graviton
        //We set with Property based escape hatches
        //https://docs.aws.amazon.com/cdk/latest/guide/cfn_layer.html#cfn_layer_raw
        /*
         * New field "Architectures" in FunctionConfiguration datatype
         * Optional, default is still x86_64
         * This new field will be a list with the following possible values: x86_64 or arm64 
         * This can be configured as a Property based Escape Hatch
         * https://docs.aws.amazon.com/cdk/latest/guide/cfn_layer.html#cfn_layer_raw
        */

        String key = "Architectures";
        JSONArray values = new JSONArray();
        values.add("arm64");
        CfnFunction cfnFunction = (CfnFunction)gravitonSimpleLambdaFunction.getNode().getDefaultChild();
        cfnFunction.addPropertyOverride(key, values);
        
    }
}
