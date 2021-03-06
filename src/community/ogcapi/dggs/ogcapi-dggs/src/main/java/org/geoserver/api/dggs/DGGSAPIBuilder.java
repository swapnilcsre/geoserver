/* (c) 2020 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.api.dggs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.geoserver.api.ConformanceDocument;
import org.geoserver.api.OpenAPIBuilder;
import org.geoserver.api.features.CollectionsDocument;
import org.geoserver.api.features.FeaturesResponse;
import org.geoserver.catalog.Catalog;

public class DGGSAPIBuilder extends OpenAPIBuilder<DGGSInfo> {

    public DGGSAPIBuilder() {
        super(DGGSAPIBuilder.class, "openapi.yaml", "DGGS 1.0 server", "ogc/dggs");
    }

    @Override
    public OpenAPI build(DGGSInfo service) {
        OpenAPI api = super.build(service);

        // adjust path output formats
        declareGetResponseFormats(api, "/", OpenAPI.class);
        declareGetResponseFormats(api, "/conformance", ConformanceDocument.class);
        declareGetResponseFormats(
                api, "/collections", org.geoserver.api.features.CollectionsDocument.class);
        declareGetResponseFormats(api, "/collections/{collectionId}", CollectionsDocument.class);
        declareGetResponseFormats(api, "/collections/{collectionId}/zones", FeaturesResponse.class);
        declareGetResponseFormats(
                api, "/collections/{collectionId}/neighbors", FeaturesResponse.class);
        declareGetResponseFormats(
                api, "/collections/{collectionId}/parents", FeaturesResponse.class);
        declareGetResponseFormats(
                api, "/collections/{collectionId}/children", FeaturesResponse.class);
        declareGetResponseFormats(api, "/collections/{collectionId}/point", FeaturesResponse.class);
        declareGetResponseFormats(
                api, "/collections/{collectionId}/polygon", FeaturesResponse.class);

        // provide a list of valid values for collectionId
        Map<String, Parameter> parameters = api.getComponents().getParameters();
        Parameter collectionId = parameters.get("collectionId");
        Catalog catalog = service.getGeoServer().getCatalog();
        List<String> validCollectionIds =
                catalog.getFeatureTypes()
                        .stream()
                        .filter(ft -> DGGSService.isDGGSType(ft))
                        .map(ft -> ft.prefixedName())
                        .collect(Collectors.toList());
        collectionId.getSchema().setEnum(validCollectionIds);

        return api;
    }
}
