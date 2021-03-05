package d3e.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

@Component
public class TransactionDeligate {

	public static interface ToRun {
		void run() throws ServletException, IOException;
	}

	@Transactional
	public void run(ToRun run) throws ServletException, IOException {
		run.run();
	}
}
