/*
 * gvNIX. Spring Roo based RAD tool for Conselleria d'Infraestructures
 * i Transport - Generalitat Valenciana
 * Copyright (C) 2010, 2011 CIT - Generalitat Valenciana
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gvnix.web.screen.roo.addon;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.addon.propfiles.PropFileOperations;
import org.springframework.roo.addon.web.mvc.controller.details.DateTimeFormatDetails;
import org.springframework.roo.addon.web.mvc.controller.details.JavaTypeMetadataDetails;
import org.springframework.roo.addon.web.mvc.controller.details.WebMetadataService;
import org.springframework.roo.addon.web.mvc.controller.scaffold.WebScaffoldAnnotationValues;
import org.springframework.roo.addon.web.mvc.controller.scaffold.mvc.WebScaffoldMetadata;
import org.springframework.roo.addon.web.mvc.controller.scaffold.mvc.WebScaffoldMetadataProvider;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.customdata.PersistenceCustomDataKeys;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.ItdTypeDetails;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.MemberHoldingTypeDetails;
import org.springframework.roo.classpath.details.annotations.StringAttributeValue;
import org.springframework.roo.classpath.itd.ItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.scanner.MemberDetails;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.support.util.Assert;

/**
 * Provides {@link PatternMetadata}. This type is called by Roo to retrieve the
 * metadata for this add-on. Use this type to reference external types and
 * services needed by the metadata type. Register metadata triggers and
 * dependencies here. Also define the unique add-on ITD identifier.
 * 
 * @author Oscar Rovira (orovira at disid dot com) at <a
 *         href="http://www.disid.com">DiSiD Technologies S.L.</a> made for <a
 *         href="http://www.cit.gva.es">Conselleria d'Infraestructures i
 *         Transport</a>
 * @author Mario Martínez (mmartinez at disid dot com) at <a
 *         href="http://www.disid.com">DiSiD Technologies S.L.</a> made for <a
 *         href="http://www.cit.gva.es">Conselleria d'Infraestructures i
 *         Transport</a>
 * @since 0.8
 */
@Component(immediate = true)
@Service
public final class PatternMetadataProvider extends AbstractPatternMetadataProvider {

    /** {@link GvNIXPattern} java type */
    public static final JavaType PATTERN_ANNOTATION = new JavaType(GvNIXPattern.class.getName());
    
    /** Name of {@link GvNIXPattern} attribute value */
    public static final JavaSymbolName PATTERN_ANNOTATION_ATTR_VALUE_NAME = new JavaSymbolName("value");

    @Reference WebScaffoldMetadataProvider webScaffoldMetadataProvider;
    @Reference ProjectOperations projectOperations;
    @Reference PropFileOperations propFileOperations;
    @Reference WebMetadataService webMetadataService;
    @Reference PatternService patternService;

    private final Map<JavaType, String> entityToWebScaffoldMidMap = new LinkedHashMap<JavaType, String>();
    private final Map<String, JavaType> webScaffoldMidToEntityMap = new LinkedHashMap<String, JavaType>();

    /**
     * The activate method for this OSGi component, this will be called by the
     * OSGi container upon bundle activation (result of the 'addon install'
     * command)
     * 
     * @param context
     *            the component context can be used to get access to the OSGi
     *            container (ie find out if certain bundles are active)
     */
    @Override
    protected void activate(ComponentContext context) {
    	
        // Next line adding a notification listener over this class allow method getLocalMidToRequest(ItdTypeDetails) to be invoked
        metadataDependencyRegistry.addNotificationListener(this);
        metadataDependencyRegistry.registerDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
        addMetadataTrigger(PATTERN_ANNOTATION);
    }

    /**
     * The deactivate method for this OSGi component, this will be called by the
     * OSGi container upon bundle deactivation (result of the 'addon uninstall'
     * command)
     * 
     * @param context
     *            the component context can be used to get access to the OSGi
     *            container (ie find out if certain bundles are active)
     */
    @Override
    protected void deactivate(ComponentContext context) {
    	
        metadataDependencyRegistry.removeNotificationListener(this);
        metadataDependencyRegistry.deregisterDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
        removeMetadataTrigger(PATTERN_ANNOTATION);
    }

    @Override
    protected String getLocalMidToRequest(ItdTypeDetails itdTypeDetails) {
    	
        // Determine the governor for this ITD, and whether any metadata is even hoping to hear about changes to that JavaType and its ITDs
        JavaType governor = itdTypeDetails.getName();
        String localMid = entityToWebScaffoldMidMap.get(governor);
        return localMid == null ? null : localMid;
    }

    /**
     * Return an instance of the Metadata offered by this add-on
     */
    @Override
    protected ItdTypeDetailsProvidingMetadataItem getMetadata(
    		String mid, JavaType aspect, PhysicalTypeMetadata controllerMetadata, String fileName) {

        // We need to parse the web scaffold annotation, which we expect to be present
        WebScaffoldAnnotationValues webScaffoldAnnotationValues = new WebScaffoldAnnotationValues(controllerMetadata);
        if (!webScaffoldAnnotationValues.isAnnotationFound() || webScaffoldAnnotationValues.getFormBackingObject() == null || 
        		controllerMetadata.getMemberHoldingTypeDetails() == null) {
        	
            return null;
        }

        JavaType controllerType = PatternMetadata.getJavaType(mid);

        // We need to know the metadata of the controller through WebScaffoldMetada
        Path path = PatternMetadata.getPath(mid);
        String webScaffoldMetadataId = WebScaffoldMetadata.createIdentifier(controllerType, path);
        WebScaffoldMetadata webScaffoldMetadata = (WebScaffoldMetadata) metadataService.get(webScaffoldMetadataId);
        if (webScaffoldMetadata == null) {
        	
            // The pattern can not be defined over a Controller without RooWebScaffold
            return null;
        }

        // We know governor type details are non-null and can be safely cast
        ClassOrInterfaceTypeDetails controllerDetails = (ClassOrInterfaceTypeDetails) controllerMetadata.getMemberHoldingTypeDetails();
        Assert.notNull(controllerDetails, "Governor failed to provide class type details, in violation of superclass contract");

        // Check if there are pattern names used more than once in project
        Assert.isTrue(!patternService.isPatternDuplicated(null), "There is a pattern name used more than once in the project");

        // Get pattern attributes of the controller
        List<StringAttributeValue> patternList = patternService.getPatternAttributes(controllerType);

        // Lookup the form backing object's metadata and check that
        JavaType entity = webScaffoldAnnotationValues.getFormBackingObject();

        PhysicalTypeMetadata entityMetadata = (PhysicalTypeMetadata) metadataService.get(
        		PhysicalTypeIdentifier.createIdentifier(entity, Path.SRC_MAIN_JAVA));
        Assert.notNull(entityMetadata, "Unable to obtain physical type metadata for type " + entity.getFullyQualifiedTypeName());
        MemberDetails entityDetails = getMemberDetails(entityMetadata);

        MemberHoldingTypeDetails entityPersistentDetails = MemberFindingUtils.getMostConcreteMemberHoldingTypeDetailsWithTag(
                        entityDetails, PersistenceCustomDataKeys.PERSISTENT_TYPE);
        SortedMap<JavaType, JavaTypeMetadataDetails> relatedApplicationTypeMetadata = webMetadataService.getRelatedApplicationTypeMetadata(
        		entity, entityDetails, mid);
        if (entityPersistentDetails == null || relatedApplicationTypeMetadata == null || relatedApplicationTypeMetadata.get(entity) == null
                || relatedApplicationTypeMetadata.get(entity).getPersistenceDetails() == null) {
        	
            return null;
        }
        
        // Remember that this entity JavaType matches up with this metadata identification string
        // Start by clearing the previous association
        // Working in the same way as WebScaffoldMetadataProvider
        JavaType oldEntity = webScaffoldMidToEntityMap.get(mid);
        if (oldEntity != null) {
        	
            entityToWebScaffoldMidMap.remove(oldEntity);
        }
        
        entityToWebScaffoldMidMap.put(entity, mid);
        webScaffoldMidToEntityMap.put(mid, entity);

        MemberDetails memberDetails = getMemberDetails(controllerMetadata);

        Map<JavaSymbolName, DateTimeFormatDetails> dateTypes = webMetadataService.getDatePatterns(
        		entity, entityDetails, mid);

        // Pass dependencies required by the metadata in through its constructor
        return new PatternMetadata(mid, aspect, controllerMetadata, webScaffoldMetadata, webScaffoldAnnotationValues, patternList, 
        		MemberFindingUtils.getMethods(memberDetails), MemberFindingUtils.getFields(memberDetails),
                relatedApplicationTypeMetadata, getRelationFieldsDetails(mid, controllerMetadata, entity, webMetadataService), 
                getRelationFieldsDateFormat(mid, controllerMetadata, entity, webMetadataService), metadataService,
                propFileOperations, projectOperations.getPathResolver(), fileManager, dateTypes);
    }

    /**
     * {@inheritDoc}, here the resulting file name will be **_ROO_GvNIXPattern.aj
     */
    @Override
    public String getItdUniquenessFilenameSuffix() {
        return "GvNIXPattern";
    }

    @Override
    protected String getGovernorPhysicalTypeIdentifier(String metadataIdentificationString) {
    	
        JavaType javaType = PatternMetadata.getJavaType(metadataIdentificationString);
        Path path = PatternMetadata.getPath(metadataIdentificationString);
        return PhysicalTypeIdentifier.createIdentifier(javaType, path);
    }

    @Override
    protected String createLocalIdentifier(JavaType javaType, Path path) {
        return PatternMetadata.createIdentifier(javaType, path);
    }

    @Override
    public String getProvidesType() {
        return PatternMetadata.getMetadataIdentiferType();
    }
    
}
