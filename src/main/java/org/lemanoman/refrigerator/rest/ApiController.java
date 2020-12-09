package org.lemanoman.refrigerator.rest;

import org.lemanoman.refrigerator.dto.ApplicationJS;
import org.lemanoman.refrigerator.dto.ApplicationMetadata;
import org.lemanoman.refrigerator.dto.VersionMetadata;
import org.lemanoman.refrigerator.model.VersionModel;
import org.lemanoman.refrigerator.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.zip.GZIPOutputStream;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/refrigerator/api/")
public class ApiController {

    @Autowired
    private StoreService storeService;

    @PostMapping("upload/{applicationName}/{versionName}")
    public VersionModel uploadArtifact(
            @PathVariable(name = "applicationName") String applicationName,
            @PathVariable(name = "versionName") String versionName,
            @RequestParam("file") MultipartFile file
    ){
        String filename = file.getOriginalFilename();

        storeService.createApplication(applicationName);
        try {
            return storeService.createVersion(applicationName,versionName,filename,file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("list/{applicationName}")
    public ApplicationJS listApplications(@PathVariable(name = "applicationName") String applicationName) throws IOException {
        return storeService.listByAppShortName(applicationName);
    }

    @GetMapping("download/{applicationName}/latest")
    public HttpEntity<byte[]> downloadLatestArtifact(
            @PathVariable(name = "applicationName") String applicationName
    ) throws IOException {

        File file = storeService.getLatestArtifact(applicationName);
        if(file == null){
            return new HttpEntity(HttpStatus.NOT_FOUND);
        }

        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

        byte[] image = new byte[(int) file.length()];
        dataInputStream.readFully(image);

        byte[] compressed = gzipCompress(image);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("content-disposition", "attachment; filename=\"" + file.getName() +"\"");
        headers.setContentLength(compressed.length);
        headers.set("Content-Encoding","gzip");

        dataInputStream.close();
        return new HttpEntity<>(compressed, headers);
    }


    @GetMapping("download/{applicationName}/{versionName}")
    public HttpEntity<byte[]> downloadArtifact(
            @PathVariable(name = "applicationName") String applicationName,
            @PathVariable(name = "versionName") String versionName
    ) throws IOException {

        File file = storeService.getArtifactByVersion(applicationName,versionName);
        if(file == null){
            return new HttpEntity(HttpStatus.NOT_FOUND);
        }

        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

        byte[] image = new byte[(int) file.length()];
        dataInputStream.readFully(image);

        byte[] compressed = gzipCompress(image);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("content-disposition", "attachment; filename=\"" + file.getName() +"\"");
        headers.setContentLength(compressed.length);
        headers.set("Content-Encoding","gzip");

        dataInputStream.close();
        return new HttpEntity<>(compressed, headers);
    }

    private byte[] gzipCompress(byte[] uncompressedData) {
        byte[] result = new byte[]{};
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(uncompressedData.length);
             GZIPOutputStream gzipOS = new GZIPOutputStream(bos)) {
            gzipOS.write(uncompressedData);
            // You need to close it before using bos
            gzipOS.close();
            result = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
