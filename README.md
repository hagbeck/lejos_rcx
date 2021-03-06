# lejos_rcx
Lejos RCX code with updates / fixes

I've forked this code to make it usable under a modern linx distribution.

In the following lines I decribe the steps to run the Lejos RCX code on newer Linux distributions. I've tried Ubuntu 12.04 LTS, Ubuntu 14.04 LTS and Linux Mint 17.3.

I've to point out that the communication betwen a VM under VirtualBox and the USB tower had failed. I think the problem was the "emulated" USB port.

## Requirements

	sudo apt-get install gcc make ant openjdk-6-jdk openjdk-7-jdk git

## Build libraries

Define `RCX_HOME`

	export RCX_HOME=/home/user/RCX

Getting sources from [https://github.com/hugheaves/lejos_rcx](https://github.com/hugheaves/lejos_rcx) or [https://github.com/hagbeck/lejos_rcx](https://github.com/hagbeck/lejos_rcx):

	git clone https://github.com/hagbeck/lejos_rcx.git

Use JDK 1.6 for the build process because of some dependencies in some classes:

	sudo update-alternatives --config java
	sudo update-alternatives --config javac

Define `LEJOS_HOME`:

	export LEJOS_HOME=$RCX_HOME/lejos_rcx/lejos

Build libraries using `ant`:

	cd $LEJOS_HOME
	ant
	
If it failes with the errors "fatal error: sys/types.h: No such file or directory" or "fatal error: stdlib.h: No such file or directory" then you are using a linux distribution without the build-essentials installed (i.e. newer Ubuntu versions or Linux Mint). Add these with

	sudo apt-get install build-essential

## Add Lejos to RCX

Use JDK 1.7 (or 1.8? I will try ;-)):

	sudo update-alternatives --config java
	sudo update-alternatives --config javac

Configure the tower:

	sudo chmod a+rw /dev/usb/legousbtower0
	export RCXTTY=/dev/usb/legousbtower0

Push to the RCX:

	$LEJOS_HOME/bin/firmdl

**I've to point out that the communication betwen a VM under VirtualBox and the USB tower had failed. I think the problem was the "emulated" USB port.**

### Check

	cd $LEJOS_HOME/release/check
	$LEJOS_HOME/bin/lejosjc DistributionSmokeTest.java
	$LEJOS_HOME/bin/lejos DistributionSmokeTest


