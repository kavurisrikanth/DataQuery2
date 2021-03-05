package rest;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;

@Configuration
@EnableWebSocket
public class GraphQLWebSocketConfig implements WebSocketConfigurer {

	@Value("${api.native.subscription:/native/subscriptions}")
	private String subscriptionPath;

	@Autowired
	private NativeSubscription subscription;

	private Map<String, GraphQLSession> subscriptions = new HashMap<>();
	
	@Bean
	public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
	    return builder.build();
	}

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new TextWebSocketHandlerImpl(), subscriptionPath).setAllowedOrigins("*");
	}

	private class TextWebSocketHandlerImpl extends TextWebSocketHandler {

		private StringBuilder partialPayload = new StringBuilder();

		@Override
		public boolean supportsPartialMessages() {
			return true;
		}

		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			subscriptions.put(session.getId(), new GraphQLSession(session));
			super.afterConnectionEstablished(session);
		}

		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
			super.afterConnectionClosed(session, status);
			GraphQLSession gql = subscriptions.get(session.getId());
			if (gql != null) {
				gql.close();
			}
		}

		@Override
		public void handleTextMessage(WebSocketSession session, TextMessage message)
				throws InterruptedException, Exception {
			partialPayload.append(message.getPayload());
			if (!message.isLast()) {
				return;
			}
			GraphQLSession gql = subscriptions.get(session.getId());
			JSONObject json = new JSONObject(partialPayload.toString());
			partialPayload = new StringBuilder();
			if (json.has("id")) {
				String id = json.getString("id");
				try {
					if (json.has("type")) {
						String type = json.getString("type");
						if ("start".equals(type)) {
							if (json.has("payload")) {
								if (gql.containsKey(id)) {
									gql.sendError(id, "Duplicate id found: " + id);
								} else {
									Flowable<JSONObject> flowable = subscription
											.subscribe(json.getJSONObject("payload"));
									gql.subscribe(id, flowable);
								}
							} else {
								gql.sendError(id, "Payload not present");
							}
						} else if ("stop".equals(type)) {
							gql.dispose(id);
						} else {
							gql.sendError(id, "Type not supported: " + type);
						}
					} else {
						gql.sendError(id, "Type not present");
					}
				} catch (Exception e) {
					gql.sendError(id, e.getMessage());
				}
			} else {
				gql.sendError(null, "Id not present");
			}
		}
	}

	private class GraphQLSession {

		private WebSocketSession session;

		private Map<String, Disposable> subscriptions = new HashMap<>();
		private Authentication auth;

		public GraphQLSession(WebSocketSession session) {
			this.session = session;
			this.auth = SecurityContextHolder.getContext().getAuthentication();
		}

		public void subscribe(String id, Flowable<JSONObject> flowable) {
			SecurityContextHolder.getContext().setAuthentication(auth);
			Disposable disposable = flowable.subscribe(m -> send(id, m), t -> sendError(id, t.getMessage()));
			subscriptions.put(id, disposable);
		}

		private void sendError(String id, String error) throws Exception {
			JSONObject ret = new JSONObject();
			if (id != null) {
				ret.put("id", id);
			}
			ret.put("type", "error");
			ret.put("error", error);
			session.sendMessage(new TextMessage(ret.toString()));
		}

		private void send(String id, JSONObject data) throws Exception {
			JSONObject ret = new JSONObject();
			ret.put("id", id);
			ret.put("type", "data");
			JSONObject payload = new JSONObject();
			payload.put("data", data);
			ret.put("payload", payload);
			String total = ret.toString();
			int limit = session.getTextMessageSizeLimit();
			while (true) {
				if (total.length() < limit) {
					session.sendMessage(new TextMessage(total));
					break;
				}
				String first = total.substring(0, limit);
				session.sendMessage(new TextMessage(first, false));
				total = total.substring(limit);
			}
		}

		public void dispose(String id) {
			Disposable disposable = subscriptions.remove(id);
			if (disposable != null) {
				disposable.dispose();
			}
		}

		public boolean containsKey(String id) {
			return subscriptions.containsKey(id);
		}

		public void close() {
			subscriptions.values().forEach(d -> d.dispose());
		}
	}
}