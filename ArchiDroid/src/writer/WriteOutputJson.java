package writer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.AppComponent;
import models.ComponentTransition;
import models.ConnectionAdapter;
import models.AppComponentAdapter;
import models.Results;

/**
 * @author - Tanjina Islam
 *
 * @date - 15-07-2019
 */
public class WriteOutputJson {

	// New 
	public static void writeToJSON(String outputDir, Set<AppComponent> appComponents, Set<ComponentTransition> componentTransitions) throws IOException {

		Results result = new Results();
		result.setAppComponents(appComponents);
		result.setCompTranitions(componentTransitions);

		Writer writer = new FileWriter(outputDir + "/output.json");

		//2. Using GsonBuilder
		Gson gson = new GsonBuilder()
				.disableHtmlEscaping()
				.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.setPrettyPrinting()
				.serializeNulls()
				.registerTypeAdapter(AppComponent.class, new AppComponentAdapter()).registerTypeAdapter(ComponentTransition.class, new ConnectionAdapter())
				.create();

		gson.toJson(result, writer);
		writer.close();
	}
}
