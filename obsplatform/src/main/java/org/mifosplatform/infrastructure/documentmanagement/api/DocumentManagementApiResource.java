/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.documentmanagement.api;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.crm.ticketmaster.command.TicketMasterCommand;
import org.mifosplatform.crm.ticketmaster.service.TicketMasterWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiConstants;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;
import org.mifosplatform.infrastructure.documentmanagement.data.DocumentData;
import org.mifosplatform.infrastructure.documentmanagement.data.FileData;
import org.mifosplatform.infrastructure.documentmanagement.service.DocumentReadPlatformService;
import org.mifosplatform.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

/**
 * @author hugo
 * 
 * this api class used to upload ,download and delete different  Documents of a client
 *
 */
@Path("{entityType}/{entityId}/documents")
@Component
@Scope("singleton")
public class DocumentManagementApiResource {

    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "parentEntityType", "parentEntityId",
            "name", "fileName", "size", "type", "description"));

    private final String SystemEntityType = "DOCUMENT";

    private final PlatformSecurityContext context;
    private final DocumentReadPlatformService documentReadPlatformService;
    private final DocumentWritePlatformService documentWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final ToApiJsonSerializer<DocumentData> toApiJsonSerializer;
    private final TicketMasterWritePlatformService ticketMasterWritePlatformService;
    @Autowired
    public DocumentManagementApiResource(final PlatformSecurityContext context,
            final DocumentReadPlatformService documentReadPlatformService, final DocumentWritePlatformService documentWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper, final ToApiJsonSerializer<DocumentData> toApiJsonSerializer,
    	    final TicketMasterWritePlatformService ticketMasterWritePlatformService) {
        this.context = context;
        this.documentReadPlatformService = documentReadPlatformService;
        this.documentWritePlatformService = documentWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.ticketMasterWritePlatformService=ticketMasterWritePlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveAllDocuments(@Context final UriInfo uriInfo, @PathParam("entityType") final String entityType,
            @PathParam("entityId") final Long entityId) {

        this.context.authenticatedUser().validateHasReadPermission(this.SystemEntityType);

        final Collection<DocumentData> documentDatas = this.documentReadPlatformService.retrieveAllDocuments(entityType, entityId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, documentDatas, this.RESPONSE_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createDocument(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @HeaderParam("Content-Length") final Long fileSize, @FormDataParam("file") final InputStream inputStream,
            @FormDataParam("file") final FormDataContentDisposition fileDetails, @FormDataParam("file") final FormDataBodyPart bodyPart,
            @FormDataParam("name") final String name, @FormDataParam("description") final String description) {
    	

        FileUtils.validateFileSizeWithinPermissibleRange(fileSize, name, ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);

        /**
         * TODO: also need to have a backup and stop reading from stream after
         * max size is reached to protect against malicious clients
         **/

        /**
         * TODO: need to extract the actual file type and determine if they are
         * permissable
         **/
        final DocumentCommand documentCommand = new DocumentCommand(null, null, entityType, entityId, name, fileDetails.getFileName(),
                fileSize, bodyPart.getMediaType().toString(), description, null);

        final Long documentId = this.documentWritePlatformService.createDocument(documentCommand, inputStream);

        return this.toApiJsonSerializer.serialize(CommandProcessingResult.resourceResult(documentId, null));
    }

    @PUT
    @Path("{documentId}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateDocument(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("documentId") final Long documentId, @HeaderParam("Content-Length") final Long fileSize,
            @FormDataParam("file") final InputStream inputStream, @FormDataParam("file") final FormDataContentDisposition fileDetails,
            @FormDataParam("file") final FormDataBodyPart bodyPart, @FormDataParam("name") final String name,
            @FormDataParam("description") final String description) {

        FileUtils.validateFileSizeWithinPermissibleRange(fileSize, name, ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);

        final Set<String> modifiedParams = new HashSet<String>();
        modifiedParams.add("name");
        modifiedParams.add("description");

        /***
         * Populate Document command based on whether a file has also been
         * passed in as a part of the update
         ***/
        DocumentCommand documentCommand = null;
        if (inputStream != null && fileDetails.getFileName() != null) {
            modifiedParams.add("fileName");
            modifiedParams.add("size");
            modifiedParams.add("type");
            modifiedParams.add("location");
            documentCommand = new DocumentCommand(modifiedParams, documentId, entityType, entityId, name, fileDetails.getFileName(),
                    fileSize, bodyPart.getMediaType().toString(), description, null);
        } else {
            documentCommand = new DocumentCommand(modifiedParams, documentId, entityType, entityId, name, null, null, null, description,
                    null);
        }
        /***
         * TODO: does not return list of changes, should be done for consistency
         * with rest of API
         **/
        final CommandProcessingResult identifier = this.documentWritePlatformService.updateDocument(documentCommand, inputStream);

        return this.toApiJsonSerializer.serialize(identifier);
    }

    @GET
    @Path("{documentId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getDocument(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("documentId") final Long documentId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.SystemEntityType);

        final DocumentData documentData = this.documentReadPlatformService.retrieveDocument(entityType, entityId, documentId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, documentData, this.RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{documentId}/attachment")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    public Response downloadFile(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("documentId") final Long documentId) {

        this.context.authenticatedUser().validateHasReadPermission(this.SystemEntityType);

        //final DocumentData documentData = this.documentReadPlatformService.retrieveDocument(entityType, entityId, documentId);
        final FileData fileData = this.documentReadPlatformService.retrieveFileData(entityType, entityId, documentId);
        final ResponseBuilder response = Response.ok(fileData.file());
        response.header("Content-Disposition", "attachment; filename=\"" + fileData.name() + "\"");
        response.header("Content-Type", fileData.contentType());
        return response.build();
    }

    @DELETE
    @Path("{documentId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteDocument(@PathParam("entityType") final String entityType, @PathParam("entityId") final Long entityId,
            @PathParam("documentId") final Long documentId) {

        final DocumentCommand documentCommand = new DocumentCommand(null, documentId, entityType, entityId, null, null, null, null, null,
                null);

        final CommandProcessingResult documentIdentifier = this.documentWritePlatformService.deleteDocument(documentCommand);

        return this.toApiJsonSerializer.serialize(documentIdentifier);
    }
    
    @POST
    @Path("{ticketId}/attachment")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces({ MediaType.APPLICATION_JSON })
	public String addTicketDetails(@PathParam("ticketId") Long ticketId,@PathParam("entityType") String entityType, @PathParam("entityId") Long entityId,
            @HeaderParam("Content-Length") Long fileSize, @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails, @FormDataParam("file") FormDataBodyPart bodyPart,
            @FormDataParam("comments") String comments, @FormDataParam("status") String status, @FormDataParam("assignedTo") Integer assignedTo,
            @FormDataParam("ticketURL") String ticketURL, @FormDataParam("problemCode") Integer problemCode, @FormDataParam("priority") String priority) {
	   

        FileUtils.validateFileSizeWithinPermissibleRange(fileSize, null, ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);

        /**
         * TODO: also need to have a backup and stop reading from stream after
         * max size is reached to protect against malicious clients
         **/

        /**
         * TODO: need to extract the actual file type and determine if they are
         * permissable
         **/
        Long createdbyId = context.authenticatedUser().getId();
        TicketMasterCommand ticketMasterCommand=new TicketMasterCommand(ticketId,comments,status,assignedTo,createdbyId,null,problemCode,priority);
        DocumentCommand documentCommand=null;
        if(fileDetails!=null&&bodyPart!=null){
         documentCommand = new DocumentCommand(null, null, entityType, entityId, null, fileDetails.getFileName(), fileSize,
        bodyPart.getMediaType().toString(), null, null);
        }else{
        	documentCommand = new DocumentCommand(null, null, entityType, entityId, null, null, fileSize,
        	        null, null, null);
        }

					Long detailId = this.ticketMasterWritePlatformService.upDateTicketDetails(ticketMasterCommand,documentCommand,ticketId,inputStream,ticketURL);
				
					return this.toApiJsonSerializer.serialize(CommandProcessingResult.resourceResult(detailId, null));
	}
}