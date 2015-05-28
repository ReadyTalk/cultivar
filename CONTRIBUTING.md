Contributing to Cultivar
========================

General
-------

 * Before you begin work submit an [Issue](https://github.com/ReadyTalk/cultivar/issues) describing the problem/bug that you are trying to solve and label it appropriately. 
 * Make sure the [changelog](https://github.com/ReadyTalk/cultivar/blob/master/CHANGES.md) is updated with any reaching changes, including the Issue number if applicable. 
 * Try to stick to a 1-branch-per-issue policy. 
 * We do not require squashing commits in the PR, but commits should be rebased on top of whatever the `master` branch is at the time the PR is made.  Every commit should, individually, pass every component of this document (e.g., tests should not be in a separate commit from the code they are testing, files should be formatted according to the guidelines on every commit, etc).  Requested updates to a PR should generally go into that PR unless requested otherwise.
 * The Issue number should also be referenced in the PR.
 * At the moment this project is built for Java 6, all code should compile and run inside of Java 6 without issue. [Issue #33](https://github.com/ReadyTalk/cultivar/issues/33) will move things to Java 7 as our minimum compile level. 

Formatting
----------

 * There is an [Eclipse formatting XML file](https://github.com/ReadyTalk/cultivar/blob/master/config/format/EclipseFormat.xml).  This should be applied to any files that you touch. 
 * There is a [checkstyle guide](https://github.com/ReadyTalk/cultivar/blob/master/config/checkstyle/checkstyle.xml). 
 * There should be no use of wildcard imports. 
 
Warnings and Static Analysis
----------------------------

 * There should be zero warnings from `javac`.
 * There should be zero checkstyle warnings.
 * There should be zero findbugs warnings. 
 
Testing
--------
 
 * All tests (unit and integration) should pass. 
 * Strive for maximizing test coverage. Write code in such a way that it can be tested and ensure that tests go in on the same commit.  
 * Tests should be named using something akin to Osherove's [naming standards for unit tests](http://osherove.com/blog/2005/4/3/naming-standards-for-unit-tests.html) which follow the form `unitOfWork_StateUnderTest_ExpectedResult`.
 * Try to test one thing per test.
 * If it needs a running instance of ZooKeeper or requires spinning up a `CuratorModule` then it is probably an integration test and belongs in the `integTest` directory.  Unit tests should generally test components in isolation from one another. 
 * Tests going forward should use scalatest as much as possible. 
 
Visibility and Documentation
----------------------------

In general:

 * [Minimize visibility on constructors](https://github.com/google/guice/wiki/KeepConstructorsHidden).
 *  Every package should have a `package.info` file with [`@ParametersAreNonnullByDefault`](https://jsr-305.googlecode.com/svn/trunk/javadoc/javax/annotation/ParametersAreNonnullByDefault.html) and a brief description in the javadoc.
 * Every publicly accessible method/class should have Javadocs. 
 * The internal `@Private` annotation should never be exposed to the outside world, rather it should be saved for use inside `PrivateModule`s. 
 