package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.request.DashboardRequest;
import com.capitalone.dashboard.request.WidgetRequest;
import com.capitalone.dashboard.service.DashboardService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
public class DashboardController {

    private final DashboardService dashboardService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);
    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
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

        Component component = new Component();
        if(request.getEnvs().toString().isEmpty()) {
            component =  dashboardService.associateCollectorToComponent(
                    request.getComponentId(), request.getCollectorItemIds());
        }else{
            //envs present
            LOGGER.info("Envs present...");
            component = dashboardService.associateCollectorToComponent(
                    request.getComponentId(), request.getEnvObjectIds());
        }
        Widget widget = dashboardService.addWidget(dashboard, request.widget());

        return ResponseEntity.status(HttpStatus.CREATED).body(new WidgetResponse(component, widget));
    }

    @RequestMapping(value = "/dashboard/{id}/widget/{widgetId}", method = PUT,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<WidgetResponse> updateWidget(@PathVariable ObjectId id,
                                                       @PathVariable ObjectId widgetId,
                                                       @RequestBody WidgetRequest request) {
        LOGGER.info("In request");
        LOGGER.info(request.getEnvs().toString());
        Component component = new Component();
        if(request.getEnvs().toString().isEmpty()) {
             component = dashboardService.associateCollectorToComponent(
                    request.getComponentId(), request.getCollectorItemIds());
        }else{
            //envs present
            LOGGER.info("Envs present...");
            component = dashboardService.associateCollectorToComponent(
                    request.getComponentId(), request.getEnvObjectIds());
        }

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
