दूत
==

Special assigment for Communication Protocols at ITBA


TODO
====

* (ask) the proxy chaining is global or per client
* filter and transformations priorities
* (ask) ip group restrictions by regex/pattern or just a list: Up to us, better if you can set groups
* (ask) is it enough with the declared media type in the http header: Yay
* (ask) what is the expected behaviour when the content length is not present or fake and there is a too-big-request filter
        (save locally until maxium limit is reached, or redirect and drop "connection" when limit is exceded)
* find out what is the criterion for defining the end of a request on a keep-alive connection
* (ask) What's the correct status code to return after a filter blocked a request.
* (ask) Transfer-Encoding and how it makes life a living hell: May not implement, but come on bro
* (ask) HTTP 1.0 compatibility: Must
* (ask) multipart mime types and filters that apply to mime types: Inspect body and apply the filters to parts

WISH LIST
=========

* pipelining

