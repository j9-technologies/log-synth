#!/bin/sh

cp=`cat 'target/streams/runtime/fullClasspath/$global/streams/export' `
java -cp "$cp" com.mapr.synth.Synth $*

