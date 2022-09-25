package ExtractTransform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import ExtractTransform.as_is.Root;

import ExtractTransform.to_be.NameCount;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static Map<String, List<String>> wayPointProcedure = new HashMap<String, List<String>>();
    private static Map<String, String> procedureAirportIcao = new HashMap<String, String>();


    /** 
     * Response event from the API Gateway Proxy for AWS Lambda
     * 
     * @param input APIGatewayProxyRequestEvent to pull the event out from the request's body.
     * @param context Context
     * @return APIGatewayProxyResponseEvent to put the extract transformed event back to the response.
     */
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
     
        try {
            final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            Map<String,String> accessControlForAPIGateway = new HashMap<String,String>(3);
          
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting(); 
            builder.serializeNulls();
            builder.registerTypeAdapterFactory(AllKeysRequiredTypeAdapterFactory.get()).create();
            Gson gson = builder.create();
            String airportJsonResponse = VarargsHttpsConnection.sendGET("https://open-atms.airlab.aero/api/v1/airac/airports");
           
            ExtractTransform.as_is.Airport[] airports = gson.fromJson(airportJsonResponse, ExtractTransform.as_is.Airport[].class);
            if (input != null) {
                Map<String, String> message =  gson.fromJson(input.getBody(), Map.class);
                String userSelectedAirport = "";
                if (message.get("airport") != null) {
                    userSelectedAirport = message.get("airport");
                } else {
                    userSelectedAirport = "";
                    throw new Exception("no such airport");
                }
                System.out.println(message);
                if (message.get("procedure").equals("sid")) {
                    System.out.println("sid event found");
                    HashMap<String, List<NameCount>> jsonIcaoWayPointSidCount = Procedure("https://open-atms.airlab.aero/api/v1/airac/sids/airport/", airports);
                    if (jsonIcaoWayPointSidCount.get(userSelectedAirport.toUpperCase()) == null) {
                        System.out.println("user selected an airport that does not exist in sids");
                    }
                    accessControlForAPIGateway.put("Access-Control-Allow-Headers", "Content-Type");
                    accessControlForAPIGateway.put("Access-Control-Allow-Origin", "");
                    accessControlForAPIGateway.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET");
                    return response.withStatusCode(200).withBody((gson.toJson(jsonIcaoWayPointSidCount)));

                } else if (message.get("procedure").equals("star")) {
                    System.out.println("star event found");
                    HashMap<String, List<NameCount>> jsonIcaoWayPointStarCount = Procedure("https://open-atms.airlab.aero/api/v1/airac/stars/airport/", airports);
                    if (jsonIcaoWayPointStarCount.get(userSelectedAirport.toUpperCase()) == null) {
                        System.out.println("user selected an airport that does not exist in stars");
                    }
                    accessControlForAPIGateway.put("Access-Control-Allow-Headers", "Content-Type");
                    accessControlForAPIGateway.put("Access-Control-Allow-Origin", "");
                    accessControlForAPIGateway.put("Access-Control-Allow-Methods", "OPTIONS,POST,GET");
                    return response.withStatusCode(200).withBody((gson.toJson(jsonIcaoWayPointStarCount)));
                } else {
                    throw new Exception("new event");
                }
            } else {
                throw new Exception("new event");
            }
        } catch (IOException ex) {
            return response
                .withBody("{}")
                .withStatusCode(500);
        } catch (Exception ex) {
            response.setStatusCode(405);
            response.setBody(ex.toString());
            return response;
        }
    }

    private HashMap<String, List<NameCount>> Procedure(String url, ExtractTransform.as_is.Airport[] airports) throws Exception {
        
        // For each airport find the top 2 waypoints that are associated with the greatest number of SIDs
        // Note that each SID is composed of a series of Waypoints

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); 
        builder.serializeNulls();
        builder.registerTypeAdapterFactory(AllKeysRequiredTypeAdapterFactory.get()).create();
        Gson gson = builder.create();

        for (int i = 0; i < airports.length; i++) {
            String procedureFromIcaoJsonResponse = VarargsHttpsConnection.sendGET(url + airports[i].getIcao());
            List<Root> procedures = Arrays.asList(gson.fromJson(procedureFromIcaoJsonResponse, ExtractTransform.as_is.Root[].class));

            // using java stream to iterate, for me to learn and test out the reduce function in java 8.
            procedures.stream().reduce((procedure1, procedure2) -> {
                String theProcedureName1 = procedure1.getName();
                // assume this is the icao name.
                String icaoName = procedure1.getAirport().getUid();
                procedureAirportIcao.put(theProcedureName1, icaoName);
                procedure1.getWaypoints().stream().reduce((wayPoint1, wayPoint2) -> {
                    String theUid = wayPoint1.getUid();
                    if (wayPointProcedure.get(theUid) != null) {
                        List<String> theSetWithDuplicates = wayPointProcedure.get(theUid);
                        theSetWithDuplicates.add(theProcedureName1);
                        wayPointProcedure.put(theUid, theSetWithDuplicates);
                    } else { 
                        List<String> theSet = new ArrayList<String>();
                        theSet.add(theProcedureName1);
                        wayPointProcedure.put(theUid, theSet);
                    }
                    theUid = wayPoint2.getUid();
                    if (wayPointProcedure.get(theUid) != null) {
                        List<String> theSetWithDuplicates = wayPointProcedure.get(theUid);
                        theSetWithDuplicates.add(theProcedureName1);
                        wayPointProcedure.put(theUid, theSetWithDuplicates);
                    } else { 
                        List<String> theSet = new ArrayList<String>();
                        theSet.add(theProcedureName1);
                        wayPointProcedure.put(theUid, theSet);
                    }
                    return wayPoint1;
                });
                String theProcedureName2 = procedure2.getName();
                icaoName = procedure2.getAirport().getUid();
                procedureAirportIcao.put(theProcedureName2, icaoName);
                procedure2.getWaypoints().stream().reduce((wayPoint1, wayPoint2) -> {
                    String theUid = wayPoint1.getUid();
                    if (wayPointProcedure.get(theUid) != null) {
                        List<String> theSetWithDuplicates = wayPointProcedure.get(theUid);
                        theSetWithDuplicates.add(theProcedureName2);
                        wayPointProcedure.put(theUid, theSetWithDuplicates);
                    } else { 
                        List<String> theSet = new ArrayList<String>();
                        theSet.add(theProcedureName2);
                        wayPointProcedure.put(theUid, theSet);
                    }
                    theUid = wayPoint2.getUid();
                    if (wayPointProcedure.get(theUid) != null) {
                        List<String> theSetWithDuplicates = wayPointProcedure.get(theUid);
                        theSetWithDuplicates.add(theProcedureName2);
                        wayPointProcedure.put(theUid, theSetWithDuplicates);
                    } else { 
                        List<String> theSet = new ArrayList<String>();
                        theSet.add(theProcedureName2);
                        wayPointProcedure.put(theUid, theSet);
                    }
                    return wayPoint1;
                });
                return procedure1;
            });
        }

       

        // the sid in waypoint sid contains duplicates
        wayPointProcedure = sortByValue(wayPointProcedure);
        HashMap<String, List<NameCount>>  jsonIcaoWayPointProcedureCount = extractTopTwo(wayPointProcedure, procedureAirportIcao);
        System.out.println(gson.toJson(jsonIcaoWayPointProcedureCount));
        return jsonIcaoWayPointProcedureCount;
    }

    private HashMap<String, List<NameCount>> extractTopTwo(Map<String, List<String>> wayPointProcedure, Map<String, String> procedureAirportIcao) throws Exception {
        List<Map.Entry<String, List<String>> > list
        = new LinkedList<Map.Entry<String, List<String>>>(
            wayPointProcedure.entrySet());
        // put data from sorted list to hashmap
        HashMap<String, List<String>> temp = new LinkedHashMap<String, List<String>>();
        // airport – the ICAO code of the airport
        // topWaypoints – array of 2 objects each consisting of these two fields 
        // i. name – the name of the waypoint
        // ii. count – the number of SIDs this waypoint is associated with (assume the SID count is unique)

        // the objective of this set of functional programming codes is to select the icao of each airport
        HashMap<String, Integer> nameCount = new HashMap<String, Integer>(temp.size());
        // icao code
        HashMap<String, HashMap<String,Integer>> result = new HashMap<String, HashMap<String, Integer>>();
        HashMap<String, List<NameCount>> jsonStructure = new HashMap<String, List<NameCount>>();  

        // check for unique icao for each waypoint's sid
        boolean isUniqueIcao = procedureAirportIcao.values().stream().distinct().limit(2).count() < 2;
        if (!isUniqueIcao) {
            throw new Exception("sids " + procedureAirportIcao.values() + " does not have the same airport code.");
        } else {
            // handle differently by creating a SID to handle two or more different icao code
            List<String> differentIcao = new ArrayList<String>();
        }
        System.out.println(procedureAirportIcao);
        for (Map.Entry<String, List<String>> eachWayPointSid : list) {
            List<String> theSids = removeDuplicates((ArrayList<String>)eachWayPointSid.getValue());
            System.out.println(theSids);
            // waypoint id and its sid unique count
            nameCount.put(eachWayPointSid.getKey(), theSids.size());
            List<String> validSids = eachWayPointSid.getValue().stream().filter((sid) -> {
                // for each sid, if the sid has an icao value, filter the validSids
                if (procedureAirportIcao.get(sid) != null) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            if (validSids != null && validSids.size() > 0) {
                String icao = procedureAirportIcao.get(validSids.get(0));
                // this part of the code handle 1 sid have many icao
                if (result.get(icao) != null) {
                    // if the airport icao has more than 1 waypoint to be sorted
                    HashMap<String,Integer> waypoints = result.get(icao);
                    waypoints.put(eachWayPointSid.getKey(), eachWayPointSid.getValue().size());
                    
                    result.put(icao, waypoints);
                } else {
                    result.put(icao, nameCount);
                }
            } 
        }

        for (Map.Entry<String, HashMap<String, Integer>> entry : result.entrySet()) {
            HashMap<String, Integer> values = entry.getValue();
            values = sortByDescendingIntegerValue(values);
            List<NameCount> nameCountList = new ArrayList<NameCount>();
            for (Map.Entry<String, Integer> entry1 : values.entrySet()) {
                String name = entry1.getKey();
                Integer value = entry1.getValue();
                NameCount nameAndCount = new NameCount();
                nameAndCount.setName(name);
                nameAndCount.setCount(value);
                nameCountList.add(nameAndCount);
            }
            if (jsonStructure.get(entry.getKey()) != null) {
                List<NameCount> oldNameCountList = jsonStructure.get(entry.getKey());
                oldNameCountList.addAll(nameCountList);
                List<NameCount> top2 = new ArrayList<NameCount>(oldNameCountList.subList(0, 2));
                jsonStructure.put(entry.getKey(), top2);
            } else {
                List<NameCount> top2 = new ArrayList<NameCount>(nameCountList.subList(0, 2));
                jsonStructure.put(entry.getKey(), top2);
            }

        }
        return jsonStructure;
    }


    private <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        // Create a new LinkedHashSet
        Set<T> set = new LinkedHashSet<>();
        // Add the elements to set
        set.addAll(list);
        // Clear the list
        list.clear();
        // add the elements of set
        // with no duplicates to the list
        list.addAll(set);
        // return the list
        return list;
    }


    // function to sort hashmap by values
    private HashMap<String, List<String>> sortByValue(Map<String, List<String>> wayPointSID2) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, List<String>> > list
            = new LinkedList<Map.Entry<String, List<String>>>(
                wayPointSID2.entrySet());

        // Sort the list using lambda expression
        Collections.sort(list, (i1, i2) -> {
            final int count1 = i1.getValue().size();
            final int count2 = i2.getValue().size();
            if (count1 > count2) {
                return -1;
            } else if (count2 > count1) {
                return 1;
            } else if (count1 == count2) {
                return 0;
            }
            return 0;
        });

        // put data from sorted list to hashmap
        HashMap<String, List<String>> temp
            = new LinkedHashMap<String, List<String>>();
        for (Map.Entry<String, List<String>> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


    private HashMap<String, Integer> sortByDescendingIntegerValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
               new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());
 
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
         
        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
