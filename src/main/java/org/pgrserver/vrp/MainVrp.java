/**
 * パッケージ名：org.pgrserver.vrp
 * ファイル名  ：MainVrp.java
 * 
 * @author mbasa
 * @since Nov 25, 2020
 */
package org.pgrserver.vrp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.pgrserver.bean.VrpServiceParamBean;
import org.pgrserver.bean.VrpShipmentBean;
import org.pgrserver.bean.VrpShipmentParamBean;
import org.pgrserver.bean.VrpServiceBean;
import org.pgrserver.bean.VrpVehicleBean;
import org.pgrserver.entity.PgrServer;
import org.pgrserver.graph.MainGraph;
import org.pgrserver.repository.CustomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.End;
import com.graphhopper.jsprit.core.problem.solution.route.activity.Start;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter.Print;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
/*
import com.graphhopper.jsprit.core.problem.job.Service;
*/

/**
 * 説明：
 *
 */
@Service
@Configurable
public class MainVrp {

    private final Logger logger = LoggerFactory.getLogger(MainVrp.class);

    /**
     * コンストラクタ
     *
     */
    public MainVrp() {
    }

    @Autowired
    CustomRepository custRepository;
    
    @Autowired
    MainGraph mainGraph;
    
    public String createServiceRoute(VrpServiceParamBean vrpServiceParamBean) {
        VehicleRoutingProblemSolution solution = 
                planSolver( vrpServiceParamBean.getVehicles(),
                        vrpServiceParamBean,null );
        
        return createRoute( solution );
    }
    
    public Map<String,Object> createServicePlan(
            VrpServiceParamBean vrpServiceParamBean) {
        
        VehicleRoutingProblemSolution solution = 
                planSolver( vrpServiceParamBean.getVehicles(),
                        vrpServiceParamBean,null );
        
        return createPlan( solution );
    }
    
    public String createShipmentRoute(
            VrpShipmentParamBean vrpShipmentParamBean) {
        
        VehicleRoutingProblemSolution solution = 
                planSolver( vrpShipmentParamBean.getVehicles(),
                        null,vrpShipmentParamBean );
        
        return createRoute( solution );
    }
    
    public Map<String,Object> createShipmentPlan(
            VrpShipmentParamBean vrpShipmentParamBean) {
        
        VehicleRoutingProblemSolution solution = 
                planSolver( vrpShipmentParamBean.getVehicles(),
                        null,vrpShipmentParamBean );
        
        return createPlan( solution );
    }
    
    public String createRoute( VehicleRoutingProblemSolution solution  ) {
        StringBuffer retVal = new StringBuffer();
        
        retVal.append("{\"type\":\"FeatureCollection\",");
        retVal.append("\"features\":[");
        
        if( !solution.getRoutes().isEmpty() ) {            
            int routeCnt = 1;
                        
            for( VehicleRoute route : solution.getRoutes() ) {
                Map<String,Object> attrib = new LinkedHashMap<String, Object>();
                
                List<Map<String,Object>> attribs = 
                        new ArrayList<Map<String,Object>>();
                List<PgrServer> pgrServer = new ArrayList<PgrServer>();

                Start start = route.getStart();
                
                attrib.put("route", routeCnt);
                attrib.put("vehicle", route.getVehicle().getId());
                attrib.put("activity", start.getName());
                attrib.put("job", "-");
                attrib.put("capacity", start.getSize().get(0)); 
                
                attribs.add(attrib);
                                
                pgrServer.add( custRepository.findNearestNode(
                        start.getLocation().getCoordinate().getX(),
                        start.getLocation().getCoordinate().getY() )
                        );
                
                for(TourActivity ta : route.getActivities() ) {
                    attrib = new LinkedHashMap<String, Object>();
                    
                    attrib.put("route", routeCnt);
                    attrib.put("vehicle", route.getVehicle().getId());
                    attrib.put("activity", ta.getName() );
                    
                    if( ta instanceof TourActivity.JobActivity ) {
                        Job job =  ((TourActivity.JobActivity) ta).getJob();
                        attrib.put("job", job.getId() );
                    }
                    else {
                        attrib.put("job", "-" );
                    }
                    
                    attrib.put("capacity", ta.getSize().get(0));                    
                    attribs.add(attrib);
                    
                    pgrServer.add( custRepository.findNearestNode(
                            ta.getLocation().getCoordinate().getX(),
                            ta.getLocation().getCoordinate().getY() )
                            );
                }
                End end = route.getEnd();
                
                attrib = new LinkedHashMap<String, Object>();
                
                attrib.put("route", routeCnt);
                attrib.put("vehicle", route.getVehicle().getId());
                attrib.put("activity", end.getName());   
                attrib.put("job", "-");   
                attrib.put("capacity", end.getSize().get(0)); 
                                 
                attribs.add(attrib);
                
                pgrServer.add( custRepository.findNearestNode(
                        end.getLocation().getCoordinate().getX(),
                        end.getLocation().getCoordinate().getY() )
                        );
                
                List<List<Integer>> pathList = new ArrayList<List<Integer>>();
                
                for(int i=0;i < pgrServer.size()-1; i++) {
                    int source = pgrServer.get(i).getSource();
                    int target = pgrServer.get(i+1).getTarget();
                    
                    pathList.add( mainGraph.dijkstraSearch(source, target) );                    
                }
                
                retVal.append(custRepository.createJsonCollectionResponse(
                                pathList,attribs,false,routeCnt*1000 ));
                routeCnt++;   
                if( routeCnt <= solution.getRoutes().size() ) {
                    retVal.append(",");
                }
            }
        }
        retVal.append("]}");
        return retVal.toString();
    }
    
    public Map<String,Object> createPlan( 
            VehicleRoutingProblemSolution solution  ) {
                
        Map<String,Object> retVal = new LinkedHashMap<String, Object>();
        retVal.put("status", "ok");
        
        Map<String,Object> solutionMap = new LinkedHashMap<String, Object>();
        solutionMap.put("costs", solution.getCost());
        solutionMap.put("noRoutes", solution.getRoutes().size());
        solutionMap.put("unassignedJobs", solution.getUnassignedJobs().size() );
        retVal.put("solution", solutionMap);
        
        
        Map<String,Object> dSolutionMap = new LinkedHashMap<String, Object>();
        List<Object> routes     = new ArrayList<Object>();
        List<Object> unAssigned = new ArrayList<Object>();
        
        dSolutionMap.put("routes", routes);
        dSolutionMap.put("unassigned", unAssigned);
        retVal.put("detailed_solution", dSolutionMap);
        
        
        if( !solution.getRoutes().isEmpty() ) {
            int routeCnt = 1;
            for( VehicleRoute route : solution.getRoutes() ) {
                
                
                Map<String,Object> mRoute = new LinkedHashMap<String, Object>();
                List<Object> mRouteParams = new ArrayList<Object>();
                
                mRoute.put("route"+routeCnt, mRouteParams);
                routes.add(mRoute);
                               
                String vehicleId = route.getVehicle().getId();
                
                Map<String,Object> mStartParam = 
                        new LinkedHashMap<String, Object>();
                Start start = route.getStart();
                mStartParam.put("vehicle", vehicleId);
                mStartParam.put("activity", start.getName());
                mStartParam.put("job", "-");
                mStartParam.put("capacity", start.getSize().get(0));
                mStartParam.put("lng", start.getLocation().getCoordinate().getX());
                mStartParam.put("lat", start.getLocation().getCoordinate().getY());
                
                mRouteParams.add(mStartParam);
                
                for(TourActivity ta : route.getActivities() ) {
                    Map<String,Object> mRouteParam = 
                            new LinkedHashMap<String, Object>();
                    
                    mRouteParam.put("vehicle", vehicleId);
                    mRouteParam.put("activity", ta.getName());
                    
                    if( ta instanceof TourActivity.JobActivity ) {
                        Job job =  ((TourActivity.JobActivity) ta).getJob();
                        mRouteParam.put("job", job.getId() );
                    }
                    else {
                        mRouteParam.put("job", "-" );
                    }
                    
                    mRouteParam.put("capacity", ta.getSize().get(0) );
                    
                    Coordinate coord = ta.getLocation().getCoordinate();                    
                    mRouteParam.put("lng", coord.getX());
                    mRouteParam.put("lat", coord.getY());
                    
                    mRouteParams.add(mRouteParam);
                }
                
                Map<String,Object> mEndParam = 
                        new LinkedHashMap<String, Object>();
                End end = route.getEnd();
                mEndParam.put("vehicle", vehicleId);
                mEndParam.put("activity", end.getName());
                mEndParam.put("job", "-");
                mEndParam.put("capacity", end.getSize().get(1));
                mEndParam.put("lng", end.getLocation().getCoordinate().getX());
                mEndParam.put("lat", end.getLocation().getCoordinate().getY());
                
                mRouteParams.add(mEndParam);
                
                routeCnt ++;
            }
        }

        if( !solution.getUnassignedJobs().isEmpty() ) {          
            for( Job uJob : solution.getUnassignedJobs() ) {
                unAssigned.add(uJob.getId());
            }
        }
                
        return retVal;
    }
    
    public VehicleRoutingProblemSolution planSolver( 
            List<VrpVehicleBean> vrpVehicles,
            VrpServiceParamBean  vrpServiceParamBean,
            VrpShipmentParamBean vrpShipmentParamBean )  {
        
        logger.info("Solving Plan");

        VehicleRoutingProblem.Builder vrpBuilder = 
                VehicleRoutingProblem.Builder.newInstance();

        int cnt = 0;
        
        for( VrpVehicleBean v : vrpVehicles ) {
            logger.info("Vehicle Capacity:"+v.getCapacity());
            cnt++;
            /**
             * VehicleType
             */
            VehicleTypeImpl.Builder vehicleTypeBuilder = 
                    VehicleTypeImpl.Builder.newInstance("vehicleType"+cnt);
            vehicleTypeBuilder.addCapacityDimension(
                    v.getWeightIndex(), v.getCapacity());
            
            VehicleType vehicleType = vehicleTypeBuilder.build();
            /**
             * VehicleBuilder
             */
            VehicleImpl.Builder vehicleBuilder = 
                    VehicleImpl.Builder.newInstance("vehicle"+cnt);
            vehicleBuilder.setStartLocation(Location.newInstance(
                    v.getStartLocation().getLng(),
                    v.getStartLocation().getLat()));
            
            vehicleBuilder.setType(vehicleType);
            /**
             * Adding Vehicle to VrpBuilder
             */
            VehicleImpl vehicle = vehicleBuilder.build();       
            vrpBuilder.addVehicle( vehicle );
        }

        /**
         * Adding Services
         */
        if( vrpServiceParamBean != null ) {
            cnt = 0;
            for( VrpServiceBean s : vrpServiceParamBean.getServices() ) {
                cnt ++;

                com.graphhopper.jsprit.core.problem.job.Service service = 
                        com.graphhopper.jsprit.core.problem.job.Service.Builder
                        .newInstance("service"+cnt)
                        .addSizeDimension(s.getWeightIndex(), s.getCapacity())
                        .setLocation(Location.newInstance(
                                s.getLocation().getLng(),s.getLocation().getLat()))
                        .build();

                vrpBuilder.addJob(service);
            }
        }

        /**
         * Adding Shipments
         */
        if( vrpShipmentParamBean != null ) {
            cnt = 0;
            for( VrpShipmentBean s : vrpShipmentParamBean.getShipment() ) {
                cnt ++;
                Shipment shipment = Shipment.Builder
                        .newInstance("shipment"+cnt)  
                        .addSizeDimension(s.getWeightIndex(), s.getCapacity())
                        .setDeliveryLocation(Location.newInstance(
                                s.getDelivery().getLng(),
                                s.getDelivery().getLat()))
                        .setPickupLocation(Location.newInstance(
                                s.getPickup().getLng(),
                                s.getPickup().getLat()))
                        .build();
                
                vrpBuilder.addJob( shipment );
            }
        }
        
        VehicleRoutingProblem problem = vrpBuilder.build();
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

        Collection<VehicleRoutingProblemSolution> solutions = 
                algorithm.searchSolutions();

        VehicleRoutingProblemSolution bestSolution = 
                Solutions.bestOf(solutions);
        
        SolutionPrinter.print(problem,bestSolution,Print.VERBOSE);
        return bestSolution;
    }
}
