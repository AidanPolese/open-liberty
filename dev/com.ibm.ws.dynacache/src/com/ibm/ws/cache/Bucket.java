// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

final class Bucket extends Queue {

	Element findByKey(Object key) {
		for (QueueElement e = head; e != null; e = e.next) {
			final Element el = (Element) e;
			if (key.equals(el.key)) {
				return el;
			}
		}

		return null;
	}

	Element replaceByKey(Object key, Object object) {
		final Element element = removeByKey(key);
		addToTail(new Element(key, object));
		return element;
	}

	void addByKey(Object key, Object object) {
		addToTail(new Element(key, object));
	}

	Element removeByKey(Object key) {
		final Element e = findByKey(key);
		if (e != null) {
			remove(e);
		}
		return e;
	}

}
