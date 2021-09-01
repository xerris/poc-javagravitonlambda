package com.myorg.lambda;

import org.json.simple.JSONObject;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.Date;

public class SimpleLambdaHandler implements RequestHandler<Object, Object>{
	
	
  @SuppressWarnings("unchecked")
  @Override
  public Object handleRequest(Object event, Context context)
  {	  
	  JSONObject response = new JSONObject();
	  try
	  {
		  
		  //perform CPU workload
		  Date startTime = new Date();
		  executeCPUIntensiveTask();	
		  Date endTime = new Date();
		  long duration = endTime.getTime() - startTime.getTime(); 
			
		  //display results
		  response.put("startTime", startTime);
		  response.put("endTime", endTime);
		  response.put("duration in Millis", duration);
			
	  }
	  catch (Exception exc)
	  {
		  return error(response, exc);
	  }

	  return ok(response);
  }

	private APIGatewayProxyResponseEvent ok(JSONObject response) {
		return new APIGatewayProxyResponseEvent()
				.withStatusCode(200)
				.withBody(response.toJSONString())
				.withIsBase64Encoded(false);
	}

	private APIGatewayProxyResponseEvent error(JSONObject response, Exception exc) {
		String exceptionString = String.format("error: %s: %s", exc.getMessage(), Arrays.toString(exc.getStackTrace()));
		response.put("Exception", exceptionString);
		return new APIGatewayProxyResponseEvent()
				.withStatusCode(500)
				.withBody(response.toJSONString())
				.withIsBase64Encoded(false);
	}
	
	public static void executeCPUIntensiveTask()
	{
		//some cpu intensive task
		long limit = 5000;
	    String lastSqRoot = "";
		for (int i=0;i<limit;i++)
		{
			for (int j=0;j<limit;j++)
			{
				double sqroot = Math.sqrt(j);
				lastSqRoot = ""+sqroot;
			}
		}
		System.out.println("lastSqRoot="+lastSqRoot);
	}
	

}