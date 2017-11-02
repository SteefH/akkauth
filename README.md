# Akkauth

Akkauth is a simple webservice for registering and authenticating accounts through Facebook login and
email address/password combinations. It is *not* a production-grade application, it only serves as a showcase of various
Scala programming techniques.

This project is subdivided into three modules:

* `akkauth-core` - This contains the business logic of Akkauth. It has no dependencies other than [Cats][cats] and
  [Simulacrum][simulacrum]. The code in this module aims to be as purely functional as possible. It exposes several
  [tagless final algebras][tagless-final] that define the "primitive" operations performed by the business logic. 
* `akkauth-impl` - This contains the implementations (a.k.a. algebra interpreters) of the algebras mentioned above, most
  of them using Akka.
* `akkauth-webservice` - This is the webservice that ties the business logic in `akkauth-core` and the interpreters in
   `akkauth-impl` together. It uses [MacWire][macwire] to wire up all the components needed to run the webservice. The
   webservice endpoints setup and serving is done through [finch][finch].


[cats]: https://typelevel.org/cats/
[simulacrum]: https://github.com/mpilquist/simulacrum
[tagless-final]: https://www.beyondthelines.net/programming/introduction-to-tagless-final/
[macwire]: https://github.com/adamw/macwire
[finch]: https://finagle.github.io/finch/
