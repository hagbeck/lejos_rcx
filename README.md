# lejos_rcx
Lejos RCX code with updates / fixes

I've forked this code to make it usable under a modern linx distribution.

In the following lines I decribe the steps to run the Lejos RCX code on an Ubuntu 12.04 LTS. I will try this on 14.04 soon.

I've to point out that a VM under VirtualBox had failed. I think the problem was the "emulated" USB port.

## Requirements

	sudo apt-get install gcc make ant openjdk-6-jdk openjdk-7-jdk

## Build libraries

Define `RCX_HOME`

	export RCX_HOME = /home/user/RCX

Getting sources from [https://github.com/hugheaves/lejos_rcx](https://github.com/hugheaves/lejos_rcx) or [https://github.com/hagbeck/lejos_rcx](https://github.com/hagbeck/lejos_rcx):

	git clone https://github.com/hagbeck/lejos_rcx.git

Use JDK 1.6 for the build process:

	sudo update-alternative --config java
	sudo update-alternative --config javac

Define `LEJOS_HOME`:

	export LEJOS_HOME = $RCX_HOME/lejos_rcx/lejos

Build libraries using `ant`:

	cd $LEJOS_HOME/build

## Add Lejos to RCX

	$LEJOS_HOME/bin/firmdl

## Use Lejos 

Use JDK 1.7:

	sudo update-alternative --config java
	sudo update-alternative --config javac

Configure the tower:

	sudo chmod a+rw /dev/usb/legousbtower0
	export RCXTTY=/dev/usb/legousbtower0


### Check

	cd $LEJOS_HOME/release/check
	$LEJOS_HOME/bin/lejosjc DistributionSmokeTest.java
	$LEJOS_HOME/bin/lejos DistributionSmokeTest


