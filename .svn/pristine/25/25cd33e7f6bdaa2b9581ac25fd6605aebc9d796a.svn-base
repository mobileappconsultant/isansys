package io.openvidu.openvidu_android.openvidu;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import com.isansys.pse_isansysportal.MainActivity;

import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpTransceiver;
import org.webrtc.SessionDescription;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.openvidu.openvidu_android.observers.CustomPeerConnectionObserver;
import io.openvidu.openvidu_android.observers.CustomSdpObserver;
import io.openvidu.openvidu_android.websocket.CustomWebSocket;

public class Session
{
    private final String TAG = "VIDEO : Session";

    private LocalParticipant localParticipant;
    private final Map<String, RemoteParticipant> remoteParticipants = new HashMap<>();
    private final String id;
    private final String token;
    private final GridLayout views_container;
    private PeerConnectionFactory peerConnectionFactory;
    private CustomWebSocket websocket;
    private final MainActivity activity;

    public Session(String id, String token, GridLayout views_container, MainActivity activity)
    {
        this.id = id;
        this.token = token;
        this.views_container = views_container;
        this.activity = activity;

        PeerConnectionFactory.InitializationOptions.Builder optionsBuilder = PeerConnectionFactory.InitializationOptions.builder(activity.getApplicationContext());
        optionsBuilder.setEnableInternalTracer(true);
        PeerConnectionFactory.InitializationOptions opt = optionsBuilder.createInitializationOptions();
        PeerConnectionFactory.initialize(opt);
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        final VideoEncoderFactory encoderFactory;
        final VideoDecoderFactory decoderFactory;
        encoderFactory = new SoftwareVideoEncoderFactory();
        decoderFactory = new SoftwareVideoDecoderFactory();

        peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .setOptions(options)
                .createPeerConnectionFactory();
    }

    public void setWebSocket(CustomWebSocket websocket) {
        this.websocket = websocket;
    }

    public PeerConnection createLocalPeerConnection() {
        final List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        PeerConnection.IceServer iceServer = PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer();
        iceServers.add(iceServer);

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.NEGOTIATE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        rtcConfig.enableDtlsSrtp = true;
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

        PeerConnection peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver("local") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                websocket.onIceCandidate(iceCandidate, localParticipant.getConnectionId());
            }
        });

        return peerConnection;
    }

    public void createRemotePeerConnection(final String connectionId) {
        final List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        PeerConnection.IceServer iceServer = PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer();
        iceServers.add(iceServer);

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.NEGOTIATE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        rtcConfig.enableDtlsSrtp = true;
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

        PeerConnection peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver("remotePeerCreation") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                websocket.onIceCandidate(iceCandidate, connectionId);
            }

            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                super.onAddTrack(rtpReceiver, mediaStreams);
                activity.setRemoteMediaStream(mediaStreams[0], remoteParticipants.get(connectionId));
            }

            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                if (PeerConnection.SignalingState.STABLE.equals(signalingState)) {
                    final RemoteParticipant remoteParticipant = remoteParticipants.get(connectionId);
                    Iterator<IceCandidate> it = remoteParticipant.getIceCandidateList().iterator();
                    while (it.hasNext()) {
                        IceCandidate candidate = it.next();
                        remoteParticipant.getPeerConnection().addIceCandidate(candidate);
                        it.remove();
                    }
                }
            }
        });

        peerConnection.addTrack(localParticipant.getAudioTrack());//Add audio track to create transReceiver
        peerConnection.addTrack(localParticipant.getVideoTrack());//Add video track to create transReceiver

        for (RtpTransceiver transceiver : peerConnection.getTransceivers()) {
            //We set both audio and video in receive only mode
            transceiver.setDirection(RtpTransceiver.RtpTransceiverDirection.RECV_ONLY);
        }

        this.remoteParticipants.get(connectionId).setPeerConnection(peerConnection);
    }

    public void createLocalOffer(MediaConstraints constraints) {
        localParticipant.getPeerConnection().createOffer(new CustomSdpObserver("local offer sdp") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                Log.i("createOffer SUCCESS", sessionDescription.toString());
                localParticipant.getPeerConnection().setLocalDescription(new CustomSdpObserver("local set local"), sessionDescription);
                websocket.publishVideo(sessionDescription);
            }

            @Override
            public void onCreateFailure(String s) {
                Log.e("createOffer ERROR", s);
            }

        }, constraints);
    }

    public String getId() {
        return this.id;
    }

    public String getToken() {
        return this.token;
    }

    public LocalParticipant getLocalParticipant() {
        return this.localParticipant;
    }

    public void setLocalParticipant(LocalParticipant localParticipant) {
        this.localParticipant = localParticipant;
    }

    public RemoteParticipant getRemoteParticipant(String id) {
        return this.remoteParticipants.get(id);
    }

    public PeerConnectionFactory getPeerConnectionFactory() {
        return this.peerConnectionFactory;
    }

    public void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        this.remoteParticipants.put(remoteParticipant.getConnectionId(), remoteParticipant);
    }

    public RemoteParticipant removeRemoteParticipant(String id) {
        return this.remoteParticipants.remove(id);
    }

    public void leaveSession()
    {
        Log.d(TAG, "leaveSession");

        AsyncTask.execute(() -> {
            websocket.setWebsocketCancelled(true);
            if (websocket != null) {
                websocket.leaveRoom();
                websocket.disconnect();
            }
            this.localParticipant.dispose();
            this.localParticipant = null;
        });

        this.activity.runOnUiThread(() -> {
            for (RemoteParticipant remoteParticipant : remoteParticipants.values())
            {
                if (remoteParticipant.getPeerConnection() != null) {
                    remoteParticipant.getPeerConnection().close();
                }
                views_container.removeView(remoteParticipant.getView());

                remoteParticipant.getVideoView().release();
            }
        });

        AsyncTask.execute(() -> {
            if (peerConnectionFactory != null) {
                peerConnectionFactory.dispose();
                peerConnectionFactory = null;
            }
        });
    }

    public void removeView(View view)
    {
        this.views_container.removeView(view);
    }

    public int getNumberOfRemoteParticipants()
    {
        return remoteParticipants.size();
    }

    public Map<String, RemoteParticipant> getRemoteParticipantsHashMap()
    {
        return remoteParticipants;
    }

}
