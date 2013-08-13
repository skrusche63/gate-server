gate-server
===========

[GATE](http://http://gate.ac.uk/) text mining is a mature platform for almost any kind of text analysis use case.

In order to seamlessly integrate GATE with other applications, platforms or frameworks, we have embedded GATE into 
a socket server. One of the intended use cases is to use GATE with a distributed real-time computation system such 
as **Storm**.

The image below illustrates a possible architecture whenn using the GATE socket server with the Storm real-time 
computing system. The architecture indicates that there may be a certain Bolt defined that opens a socket 
connection to determine a set of named entities from an incoming text message, e.g. from Twitter.


![Storm Use Case](https://raw.github.com/skrusche63/gate-server/master/socket/docs/Storm%20Use%20Case.png)

