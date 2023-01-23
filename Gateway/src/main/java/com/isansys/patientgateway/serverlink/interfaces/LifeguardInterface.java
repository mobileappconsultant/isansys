package com.isansys.patientgateway.serverlink.interfaces;

import com.isansys.patientgateway.serverlink.model.CheckDeviceStatusRequest;
import com.isansys.patientgateway.serverlink.model.CheckDeviceStatusResponse;
import com.isansys.patientgateway.serverlink.model.GetBedDetailsResponse;
import com.isansys.patientgateway.serverlink.model.GetDeviceFirmwareByBedIdRequest;
import com.isansys.patientgateway.serverlink.model.GetDeviceFirmwareByIdResponse;
import com.isansys.patientgateway.serverlink.model.GetDeviceFirmwareListRequest;
import com.isansys.patientgateway.serverlink.model.GetDeviceFirmwareListResponse;
import com.isansys.patientgateway.serverlink.model.GetGatewayConfigRequest;
import com.isansys.patientgateway.serverlink.model.GetGatewayConfigResponse;
import com.isansys.patientgateway.serverlink.model.GetMQTTCertificateResponse;
import com.isansys.patientgateway.serverlink.model.GetServerConfigurableTextRequest;
import com.isansys.patientgateway.serverlink.model.GetServerConfigurableTextResponse;
import com.isansys.patientgateway.serverlink.model.GetWardDetailsResponse;
import com.isansys.patientgateway.serverlink.model.GetWebPagesResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface LifeguardInterface
{
    @POST("Configuration")
    Call<List<GetGatewayConfigResponse>> getGatewayConfig(
            @Body GetGatewayConfigRequest request
    );

    @POST("WardDetailsLIST")
    Call<List<GetWardDetailsResponse>> getWardDetailsList();

    @POST("BedDetailsLIST")
    Call<List<GetBedDetailsResponse>> getBedDetailsList();

    @POST("DeviceStatusASK")
    Call<CheckDeviceStatusResponse> checkDeviceDetails(
            @Body CheckDeviceStatusRequest request
    );

    @POST("DeviceFirmwareLIST")
    Call<List<GetDeviceFirmwareListResponse>> getDeviceFirmwareList(
            @Body GetDeviceFirmwareListRequest request
    );

    @POST("DeviceFirmwareById")
    Call<GetDeviceFirmwareByIdResponse> getDeviceFirmwareById(
            @Body GetDeviceFirmwareByBedIdRequest request
    );

    @Multipart
    @POST("LogFileUpload")
    Call<ResponseBody> upload(
            @Part("file_name") RequestBody file_name,
            @Part("ward_name") RequestBody ward_name,
            @Part("bed_name") RequestBody bed_name,
            @Part MultipartBody.Part file
    );

    @POST("ServerConfigurableTextLIST")
    Call<List<GetServerConfigurableTextResponse>> getServerConfigurableText(
            @Body GetServerConfigurableTextRequest request
    );

    @POST("ViewableWebPageLIST")
    Call<List<GetWebPagesResponse>> getWebPages(
    );

    @POST("MQTTCertificateRequest")
    Call<GetMQTTCertificateResponse> getMQTTCertificateFromServer();
}
