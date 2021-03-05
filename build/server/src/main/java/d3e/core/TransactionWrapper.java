package d3e.core;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionWrapper {

	@Autowired
	private D3ESubscription subscription;

	@Autowired
	private TransactionDeligate deligate;

	public void doInTransaction(TransactionDeligate.ToRun run) throws ServletException, IOException {
		boolean created = createTransactionManager();
		try {
			deligate.run(run);
			if (created) {
				publishEvents();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (created) {
				TransactionManager.remove();
			}
		}
	}

	private void publishEvents() throws ServletException, IOException {
		TransactionManager manager = TransactionManager.get();
		TransactionManager.remove();
		if (manager.isEmpty()) {
			return;
		}
		createTransactionManager();
		deligate.run(() -> {
			manager.commit(event -> {
				try {
					subscription.handleContextStart(event);
				} catch (Exception e) {
				}
			});
		});
		publishEvents();
	}

	private boolean createTransactionManager() {
		TransactionManager manager = TransactionManager.get();
		if (manager == null) {
			manager = new TransactionManager();
			TransactionManager.set(manager);
			return true;
		}
		return false;
	}
}
