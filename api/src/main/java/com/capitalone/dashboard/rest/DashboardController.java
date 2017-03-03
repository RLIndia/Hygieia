package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.request.DashboardRequest;
import com.capitalone.dashboard.request.WidgetRequest;
import com.capitalone.dashboard.service.CollectorService;
import com.capitalone.dashboard.service.DashboardService;
import com.capitalone.dashboard.request.CollectorItemRequest;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class DashboardController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardService dashboardService;
    private final CollectorService collectorService;

    @Autowired
    public DashboardController(DashboardService dashboardService, CollectorService collectorService) {
        this.dashboardService = dashboardService;
        this.collectorService = collectorService;
    }



    @RequestMapping(value = "/dashboard", method = GET, produces = APPLICATION_JSON_VALUE)
    public Iterable<Dashboard> dashboards() {
        return dashboardService.all();
    }

    @RequestMapping(value = "/dashboard", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Dashboard> createDashboard(@Valid @RequestBody DashboardRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)

                .body(dashboardService.create(request.toDashboard()));
    }


    @RequestMapping(value = "/setupdashboard/{id}", method = POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)

    public ResponseEntity<Dashboard> createAndConfigDashboard(@PathVariable ObjectId id, @RequestBody List<WidgetRequest> request) {
        Dashboard dashboard = dashboardService.get(id);

        //dashboard should have the dashboardID and componentID
        List<Component> tempComponents = dashboard.getApplication().getComponents();
        Component firstComponent = tempComponents.get(0);
        try{
            LOGGER.info(firstComponent.getId().toString());
        }catch(Exception e){
            LOGGER.info(e.getMessage());
        }


        //loop through each widget in the request
        for(WidgetRequest widget : request) {
            //Create the collector Item if not present for Github
            if(widget.getName().equals("repo")){
                CollectorItem ci = new CollectorItem();
                Map<String, Object> options = new HashMap<>();
                Map<String, Object> scm = (Map<String, Object>) widget.getOptions().get("scm");
                if(scm.get("value").equals("GitHub"))
                    options.put("scm","Github");
                else
                    options.put("scm",(String) scm.get("value"));
                options.put("branch", widget.getOptions().get("branch"));
                options.put("url", widget.getOptions().get("url"));
                ci.setOptions(options);

                ObjectId id1 =  new ObjectId((String)widget.getOptions().get("collectorId"));
                ci.setCollectorId(id1);
                ci.setEnabled(true);
                ci.setPushed(true);

                //To do find if there is an item with the same options and collectorid present
                //collectorService.getAllCollectorItemsByCollectorId()
                CollectorItem ciNew = collectorService.createCollectorItem(ci);
                LOGGER.info("created new collector item");
                List<ObjectId> cids = new ArrayList<>();
                cids.add(ciNew.getId());
                widget.setCollectorItemIds(cids);

                LOGGER.info("collector item ids " + cids.toString());
                Component component = dashboardService.addCollectorItemToComponent(firstComponent.getId(),widget.getCollectorItemIds());
            }
            else {
                Component component = dashboardService.associateCollectorToComponent(
                        firstComponent.getId(), widget.getCollectorItemIds());
            }





            Widget newwidget = dashboardService.addWidget(dashboard, widget.widget());
            LOGGER.info("Widget:" + widget.getName() + " setup");
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Access-Control-Allow-Origin", "*")
                .body(dashboard);
    }

    @RequestMapping(value = "/dashboard/collectors", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<Collector>> getDashboardCollectors() {
        LOGGER.info("Hit");
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Access-Control-Allow-Origin","*")
                .header("Access-Control-Allow-Methods","GET")
                .body( dashboardService.getDashboardCollectors());


    }




    @RequestMapping(value = "/dashboard/{id}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public Dashboard getDashboard(@PathVariable ObjectId id) {
        return dashboardService.get(id);
    }

    @RequestMapping(value = "/dashboard/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateDashboard(@PathVariable ObjectId id,
                                                  @RequestBody DashboardRequest request) {
        dashboardService.update(request.copyTo(dashboardService.get(id)));
        return ResponseEntity.ok("Updated");
    }

    @RequestMapping(value = "/dashboard/{id}", method = DELETE)
    public ResponseEntity<Void> deleteDashboard(@PathVariable ObjectId id) {
        dashboardService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/dashboard/{id}/widget", method = POST,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<WidgetResponse> addWidget(@PathVariable ObjectId id, @RequestBody WidgetRequest request) {

        Dashboard dashboard = dashboardService.get(id);

        Component component = dashboardService.associateCollectorToComponent(
                request.getComponentId(), request.getCollectorItemIds());

        Widget widget = dashboardService.addWidget(dashboard, request.widget());

        return ResponseEntity.status(HttpStatus.CREATED).body(new WidgetResponse(component, widget));
    }

    @RequestMapping(value = "/dashboard/{id}/widget/{widgetId}", method = PUT,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<WidgetResponse> updateWidget(@PathVariable ObjectId id,
                                                       @PathVariable ObjectId widgetId,
                                                       @RequestBody WidgetRequest request) {
        Component component = dashboardService.associateCollectorToComponent(
                request.getComponentId(), request.getCollectorItemIds());

        Dashboard dashboard = dashboardService.get(id);
        Widget widget = request.updateWidget(dashboardService.getWidget(dashboard, widgetId));
        widget = dashboardService.updateWidget(dashboard, widget);

        return ResponseEntity.ok().body(new WidgetResponse(component, widget));
    }

    @RequestMapping(value = "/dashboard/mydashboard", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public List<Dashboard> getOwnedDashboards(@RequestParam String username) {
        List<Dashboard> myDashboard = dashboardService.getOwnedDashboards(username);
        return myDashboard;

    }

    @RequestMapping(value = "/dashboard/myowner/{dashboardtitle}", method = GET,
            produces = APPLICATION_JSON_VALUE)
    public String getDashboardOwner(@PathVariable String dashboardtitle) {
        String dashboardOwner = "No Owner defined";
        if (null != dashboardtitle) {
            dashboardOwner = dashboardService.getDashboardOwner(dashboardtitle);
        }
        return dashboardOwner;
    }
}
