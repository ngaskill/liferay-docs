# Foundations: JAX-RS, OSGi Whiteboard & APIO. [](id=rest-apis-foundations)

There are three cornerstones upon which the Hypermedia REST APIs are built in @product@. 

First and foremost, these APIs are REST services, so it should not be a surprise that they are written according to the [JAX-RS Specification](http://download.oracle.com/otndocs/jcp/jaxrs-2_1-final-eval-spec/index.html). Although it could seem as a common sense start, keep in mind that the Hypermedia REST APIs is composed of a set of JAX-RS Services. Thus, if you find yourself in need of extending the API, in the end, it's just a matter of creating new JAX-RS services.

But, those services are 

