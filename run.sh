#!/bin/sh

classpathfile='/Users/reid/Code/J9/log-synth/target/streams/runtime/fullClasspath/$global/streams/export'
cp=`cat $classpathfile`
java -cp "$cp" j9.logsynth.LogSynth $*

