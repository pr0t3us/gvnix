/*
 * gvNIX. Spring Roo based RAD tool for Conselleria d'Infraestructures i
 * Transport - Generalitat Valenciana Copyright (C) 2010 CIT - Generalitat
 * Valenciana
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.gvnix.service.roo.addon.security;

import javax.security.auth.callback.CallbackHandler;

import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.gvnix.service.roo.addon.annotations.GvNIXWebServiceSecurity;
import org.gvnix.service.roo.addon.ws.importt.WSImportOperations;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.itd.AbstractItdMetadataProvider;
import org.springframework.roo.classpath.itd.ItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.LogicalPath;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.ProjectOperations;

/**
 * <p>
 * Web Service Security Metadata provider
 * </p>
 * <p>
 * Manage {@link GvNIXWebServiceSecurity} annotation
 * </p>
 * <p>
 * In {@link #getMetadata(String, JavaType, PhysicalTypeMetadata, String)}:
 * <ul>
 * <li>creates Class ITD to make governor class implements
 * {@link CallbackHandler}</li>
 * <li>creates <code>{governor_Class_Name}Sercurity.properties</code> file in
 * <code>scr/main/resorces/{governor_Package}</code></li>
 * <li>updates <code>client-config.wsdd</code> file using
 * {@link SecurityService}</li>
 * <li>checks if {certificate} file exist in
 * <code>scr/main/resorces/{governor_Package}</code></li>
 * </ul>
 * </p>
 * 
 * @author Jose Manuel Vivó Arnal ( jmvivo at disid dot com ) at <a
 *         href="http://www.disid.com">DiSiD Technologies S.L.</a> made for <a
 *         href="http://www.cit.gva.es">Conselleria d'Infraestructures i
 *         Transport</a>
 */
@Component
@Service
public final class WSServiceSecurityMetadataProvider extends
        AbstractItdMetadataProvider {

    @Reference
    private SecurityService securityService;

    @Reference
    private WSImportOperations operationsService;

    @Reference
    private ProjectOperations projectOperations;

    /**
     * The activate method for this OSGi component, this will be called by the
     * OSGi container upon bundle activation (result of the 'addon install'
     * command)
     * 
     * @param context the component context can be used to get access to the
     *        OSGi container (ie find out if certain bundles are active)
     */
    protected void activate(ComponentContext context) {
        metadataDependencyRegistry.registerDependency(
                PhysicalTypeIdentifier.getMetadataIdentiferType(),
                getProvidesType());
        addMetadataTrigger(new JavaType(GvNIXWebServiceSecurity.class.getName()));
    }

    /**
     * The deactivate method for this OSGi component, this will be called by the
     * OSGi container upon bundle deactivation (result of the 'addon uninstall'
     * command)
     * 
     * @param context the component context can be used to get access to the
     *        OSGi container (ie find out if certain bundles are active)
     */
    protected void deactivate(ComponentContext context) {
        metadataDependencyRegistry.deregisterDependency(
                PhysicalTypeIdentifier.getMetadataIdentiferType(),
                getProvidesType());
        removeMetadataTrigger(new JavaType(
                GvNIXWebServiceSecurity.class.getName()));
    }

    /**
     * <p>
     * Return an instance of the Metadata offered by this add-on and perform
     * this operations:
     * <ul>
     * <li>creates Class ITD to make governor class implements
     * {@link CallbackHandler}</li>
     * <li>creates <code>{governor_Class_Name}Sercurity.properties</code> file
     * in <code>scr/main/resorces/{governor_Package}</code></li>
     * <li>updates <code>client-config.wsdd</code> file using
     * {@link SecurityService}</li>
     * <li>checks if {certificate} file exist in
     * <code>scr/main/resorces/{governor_Package}</code></li>
     * </ul>
     * </p>
     */
    @Override
    protected ItdTypeDetailsProvidingMetadataItem getMetadata(
            String metadataIdentificationString, JavaType aspectName,
            PhysicalTypeMetadata governorPhysicalTypeMetadata,
            String itdFilename) {

        // Setup project
        securityService.setupWSSJ4();

        JavaType serviceClass = governorPhysicalTypeMetadata.getType();
        String serviceName = operationsService.getServiceName(serviceClass);

        String certificate = operationsService.getCertificate(serviceClass);

        // create Metadata
        WSServiceSecurityMetadata metadata = new WSServiceSecurityMetadata(
                metadataIdentificationString, aspectName,
                governorPhysicalTypeMetadata, serviceName, certificate);

        // Check properties file
        String propertiesPath = WSServiceSecurityMetadata
                .getPropertiesPath(serviceClass);
        String propertiesAbsolutePath = projectOperations.getPathResolver()
                .getIdentifier(
                        LogicalPath.getInstance(Path.SRC_MAIN_RESOURCES, ""),
                        propertiesPath);

        Validate.isTrue(
                fileManager.exists(propertiesAbsolutePath),
                "Missing certificated file '"
                        .concat(Path.SRC_MAIN_RESOURCES.name()).concat("/")
                        .concat(propertiesPath).concat("' for ")
                        .concat(governorPhysicalTypeMetadata.getId()));

        // Checks for certificated file
        String certificatePath = metadata.getCertificatePath();
        String certificateAbsolutePath = projectOperations.getPathResolver()
                .getIdentifier(
                        LogicalPath.getInstance(Path.SRC_MAIN_RESOURCES, ""),
                        certificatePath);

        Validate.isTrue(
                fileManager.exists(certificateAbsolutePath),
                "Missing certificated file '"
                        .concat(Path.SRC_MAIN_RESOURCES.name()).concat("/")
                        .concat(certificatePath).concat("' for ")
                        .concat(governorPhysicalTypeMetadata.getId()));

        return metadata;

    }

    /**
     * Define the unique ITD file name extension, here the resulting file name
     * will be **_ROO_Security.aj
     */
    public String getItdUniquenessFilenameSuffix() {
        return "GvNIX_WebSecurity";
    }

    @Override
    protected String getGovernorPhysicalTypeIdentifier(
            String metadataIdentificationString) {
        JavaType javaType = WSServiceSecurityMetadata
                .getJavaType(metadataIdentificationString);
        LogicalPath path = WSServiceSecurityMetadata
                .getPath(metadataIdentificationString);
        return PhysicalTypeIdentifier.createIdentifier(javaType, path);
    }

    @Override
    protected String createLocalIdentifier(JavaType javaType, LogicalPath path) {
        return WSServiceSecurityMetadata.createIdentifier(javaType, path);
    }

    public String getProvidesType() {
        return WSServiceSecurityMetadata.getMetadataIdentiferType();
    }
}