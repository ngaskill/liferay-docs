# Foundations: JAX-RS, OSGi Whiteboard & APIO. [](id=rest-apis-foundations)

There are three cornerstones upon which the Hypermedia REST APIs are built in @product@. 

Although is not completely necessary to master the three of them, it is useful to have at least a good basic understanding of what these are, and which role they play to make the APIs work.

## JAX-RS [](id=jax-rs)
First and foremost, these APIs are REST services, so it should not be a surprise that they are written according to the [JAX-RS Specification](http://download.oracle.com/otndocs/jcp/jaxrs-2_1-final-eval-spec/index.html). 

JAX-RS API described in the aforementioned specification drives the [development of RESTful Web services](https://docs.oracle.com/javaee/7/tutorial/jaxrs.htm) by the use of Java annotations that allow developers to decorate Java POJOS (regular classes) that define the resources, endpoints, mappings and all the elements required to publish a REST Web service.

Although it could seem as a common sense start, keep in mind that the Hypermedia REST APIs is composed of a set of JAX-RS Services. Thus, if you find yourself in need of extending the API, in the end, it's just a matter of creating new JAX-RS services. But there are other pieces that will help the developer to create or consume those services.

## OSGi's JAX-RS Whiteboad Specification [](id=osgi-jaxrs-whiteboard)

The REST services that compose the Hypermedia REST APIs should be developed and deployed, as modules, keeping in mind the OSGi environment in which is based @product@ since version 7.

To bootstrap and publish JAX-RS services, register them as OSGi components and resolve posible references to other OSGi components (or services) already deployed as part of other modules, we make use of the [OSGi's JAX-RS Whiteboard](https://osgi.org/specification/osgi.cmpn/7.0.0/service.jaxrs.html#service.jaxrs.whiteboard).

The [OSGi's JAX-RS Whiteboard](https://osgi.org/specification/osgi.cmpn/7.0.0/service.jaxrs.html#service.jaxrs.whiteboard) is the mechanism inside the OSGi container which implements the [whiteboard pattern](https://en.wikipedia.org/wiki/Whiteboard_Pattern) to boostrap the JAX-RS runtime with OSGi modules. This pattern allows a better management of the lifecycle of the components and dependencies between them that [suits better the needs of the OSGi environment (PDF)](https://www.osgi.org/wp-content/uploads/whiteboard1.pdf).

The good news is that you, as developer of new services or applications that consume the Hypermedia REST API, don't need to know too much detail about how the whiteboard specification works. Simply keep in mind that the services are, as any other component in @product@, developed and registered as OSGi components wrapped around JAXS-RS services.

## APIO Architect [](id=apio-architect)

Besides using [regular JAX-RS services](#jax-rs) and registering those as components on the OSGi container through the [OSGi's JAX-RS whiteboard](#osgi-jaxrs-whiteboard) the main goal in the design of @product@'s API is to leverage hypermedia as a key enabler of evolvable and easily usable APIs by a wide bunch of different apps and API customers.

But time has proven that writting hypermedia and evolvable APIs is not easy. So, in an effort to ease that burden to the developers of APIs from this difficulties we, in Liferay, are promoting the initiative of [Evolvable APIS](https://evolvable-apis.org/) as and have created the APIO project and one of its components: the [Apio Architect](https://github.com/liferay/com-liferay-apio-architect), a toolkit that encourages and facilitates an opinionanted way of building hypermedia REST Services on top of JAX-RS and OSGi's JAX-RS Whiteboard.

Apio Architect transparently adds support for several Hypermedia Formats (as HAL, Siren or JSON-LD), Content-negotation, links navigability and discoverability of resources, collection patterns, and many other features that helps to build hypermedia REST APIs easily.

Apio Architect is probably the most important component in how Hypermedia REST APIs are built. Through the rest of the documents you will hopefully see the benefits that it brings to the services. 

