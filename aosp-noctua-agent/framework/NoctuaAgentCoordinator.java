package com.android.server.noctua;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.noctua.aosp.ForecastResult;
import com.noctua.aosp.INoctuaAgent;

/**
 * Minimal policy/action half of an OS-level agent, for an AOSP system service.
 *
 * The included native daemon is only the inference engine. A production agent
 * calls this coordinator from a user-consented trigger (JobScheduler, a health
 * data update, or an explicit request), then applies a transparent local policy.
 */
public final class NoctuaAgentCoordinator {
    private static final String INFERENCE_SERVICE = "noctua_agent";
    private static final String CHANNEL_ID = "noctua_wellness";
    private static final int NOTIFICATION_ID = 1001;
    private static final int RECOVERY_THRESHOLD = 60;

    private final Context context;

    public NoctuaAgentCoordinator(Context context) {
        this.context = context;
    }

    public AgentDecision onFeaturesReady(float[] features) throws RemoteException {
        IBinder binder = ServiceManager.waitForService(INFERENCE_SERVICE);
        INoctuaAgent agent = INoctuaAgent.Stub.asInterface(binder);
        if (agent == null) {
            throw new RemoteException("Noctua inference service unavailable");
        }

        ForecastResult forecast = agent.forecast(features);
        AgentAction action = forecast.readinessScore < RECOVERY_THRESHOLD
                ? AgentAction.SUGGEST_RECOVERY
                : AgentAction.NO_ACTION;
        dispatch(action);
        return new AgentDecision(forecast, action);
    }

    private void dispatch(AgentAction action) {
        if (action != AgentAction.SUGGEST_RECOVERY) {
            return;
        }

        // ACT is deliberately a suggestion, not a silent DND/network/power
        // change. The notification contains no raw biometric history.
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(new NotificationChannel(
                CHANNEL_ID, "Noctua wellness", NotificationManager.IMPORTANCE_DEFAULT));
        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Noctua recovery suggestion")
                .setContentText("Your on-device forecast suggests a lighter day.")
                .setAutoCancel(true)
                .build();
        manager.notify(NOTIFICATION_ID, notification);
    }

    public enum AgentAction {
        NO_ACTION,
        SUGGEST_RECOVERY,
    }

    public static final class AgentDecision {
        public final ForecastResult forecast;
        public final AgentAction action;

        AgentDecision(ForecastResult forecast, AgentAction action) {
            this.forecast = forecast;
            this.action = action;
        }
    }
}
