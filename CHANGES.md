Changelog
=========

1.2.0
-----

* Set license as Apache.
* Build changes (Issue #11, Issue #25).

1.3.0
-----

* Upgraded dependencies (Issue #25).
* Added namespaces (Issue #31). 
* Added CuratorUninterruptibles.
* Parameterized AbstractZookeeperClusterTest (Issue #23).
* Minor documentation fixes.
* Easier registration for discovery (Issue #10).
* Improved logging.
* Improved documentation.
* Easier unregistration/shutdown using either `java.lang.Runtime` shutdown hooks or customized shutdown systems (Issue #20).
* Utilities for shutdown in a servlet environment (Issue #21). 
* ImmutableProperties payload object (Issue #40).

1.4.0
-----

* Updated Curator to 2.7.0 and Checkstyle to 6.0.
* Added a CONTRIBUTING.md file.
* Upgraded gradle (2.2.1), JUnit (4.12), and mockito (1.10.8) (Issue #37).
* Minor documentation improvements.

1.5.0
-----

* Namespace change (Issue #52).
* Upgrading Curator to 2.7.1 (Issue #53).
* Upgrading Gradle to 2.3.
* Fixed lack of `CuratorService`s preventing demo code from working (Issue #55).
* Adding property override to NodeContainer (Issue #54).
* Adding tools to facilitate easier testing and rapid prototyping (Issue #60).
* Added the NodeCacheWrapper (Issue #62).
* Added the ServiceProviderWrapper (Issue #62).