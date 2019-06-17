[![Build Status](https://travis-ci.org/fmohr/PROSECO.svg?branch=master)](https://travis-ci.org/fmohr/PROSECO)
s
# PROSECO
## Table of Contents
* [Overview](#overview)
* [Demo](#demo)
* [Introduction and Usage](#introduction-and-usage)
* [Setup of a Prototype Folder](#setup-of-a-prototype-folder)
* [Starting The Server](#starting-the-server)

## Overview
The PROtotype-based SErvice COmposition (PROSECO) Framework is a free and open-source Java tool for automated service composition. PROSECO uses prototypes as basis of the services, and builds up on these prototypes by gathering user input. Services could be any type of server application. Current examples are automated machine learning and cloud gaming.

![alt text](https://github.com/fmohr/PROSECO/raw/development/doc/img/proseco-overview.png "PROSECO Overview")

#### Prototype Selection: 
In the Prototype Selection phase, user provides an input (currently as keywords i.e. `"imageclassification"`, `"automl"`, `"game"`) and the suitable prototype for the input is selected.

#### Prototype Interview:
In the interview phase, further questions are asked to the user to gather necessary parameters for the prototype. For example, which game does the user want to play? or uploading training files for a machine learning service.

#### Finding Best Solution:
In this phase, the best solution for users requirements are found. For example, for automated machine learning service, best possible machine learning pipeline that suits the uploaded training file is the best solution. 

#### Run and Present Solution as a Service:
Finally, an access point to the best solution is presented to the user. An access point can be a game client for cloud gaming service or a web application that the user can use for machine learning.

## Demo

#### Cloud Gaming
![Cloud Gaming](https://thumbs.gfycat.com/GenuineRealFlyingfish-size_restricted.gif)

#### Automated Machine Learning
![Automated Machine Learning](https://thumbs.gfycat.com/SpitefulRegalAmericancreamdraft-size_restricted.gif)


## Introduction and Usage
PROSECO creates executable services based on *prototypes*.
The prototypes are the folders within the "prototypes" directory, which should contain "almost"-runnable software packages.
A prototype folder basically consists of (i) a folder "src" containing the actual software package and (ii) control files that determine how the grounding of that particular prototype works; a detailed description of the requirements is given below.

You run the PROSECO algorithm with a task keyword, e.g., "gender recognition", and a set of data in a format appropriate for the task (usually a .zip file). Given these two inputs, PROSECO identifies the prototype that matches the keyword best.
Ggenerally, PROSECO supports an ontological matching, but currently only an exact match is implemented; hence, there must be a folder in the "prototypes" directory that corresponds to the query keyword. PROSECO then executes the search algorithms in the "strategies" sub-directory, selects the best solution, and deploys the final service composition in a new sub-directory within your current working directory (from where you call PROSECO).

An exemplary prototype that comes with PROSECO by default is in the context of On-The-Fly Machine Learning (OTF-ML). OTF-ML aims at providing machine learning functionality to non-expert end-users. The concrete problem addressed is the one of gender recognition. The requested service is a machine learning algorithm, a classifier, that decides whether the person on a given picture (given as .jpeg or .png) is male or female. Different from AutoML approaches (e.g., AutoWEKA AutoSKLearn), PROSECO understands ML algorithms as services. In this sense, ML libraries represent repositories of such services. Using [On-The-Fly Computing](https://sfb901.uni-paderborn.de) paradigms, the computations for algorithm selection and configuration are meant to be executed in the cloud by some OTF-ML provider. Since the computations for selection and configuration might be very demanding for CPU and memory, AutoML approaches are heavily limited by the hardware specifications of the user. In contrast to this strong limitation, OTF-ML benefits from the rich hardware resources of compute centers enabling to leverage parallelization in order to speed up the overall process. 

## Setup of a Domain Folder
    .
    ├── interview                     # Used for gathering required information to run the service
        ├── interview.yaml            # Interview process definitions
        └── questions.yaml            # Interview questions repository
    ├── prototypes
        └── prototype_name            # A domain can have multiple protoypes
            ├── strategies                # Contains search strategies to derive concrete compositions
                └── strategy_name             
                    └── run.bat               # Used for launching the strategy
            │...
            ├── deployment.bat         # deploys final configuration
            └── grounding.bat          # Replaces the placeholders of the prototype by concrete values and builds the package

## Project Structure
- PROSECO: Parent Project
- commons: contains common classes like configs, models, utils
- core: main composition algorithm
- webserver: web server module

## Starting The Server
A web server is contained withing PROSECOs webserver sub-module to provide an interface to the user. A Prototype-specific interview is presented via this web server to get necessary inputs of the prototype from the user.
 * Run `de.upb.crc901.proseco.view.app.PROSECOServer` from webserver sub-module as java application to start up the server
 * You can then access the web application via `http://localhost:8080`
 * Fill the input with `ic` for imageclassification prototype, `automl` for AutoML prototype, or `game` for cloud gaming prototype
 * You will be presented with the interview of selected prototype, fill the necessary inputs of the interview
 * At the end of the interview, best solution will be presented as result
 


## Predefined Semantic of Parameters for Runnables ##
###strategy runnables (run.bat or run.sh in the strategy folders)###
Takes 4 arguments:
 #1 PROSECO process id
 #2 full path of folder with input resources
 #3 full path of the output folder of the strategy
 #4 timeout in seconds
 
###grounding executable (grounding.bat or grounding.sh)###
Takes 3 arguments:
 #1 PROSECO process id
 #2 full path of the output folder of the selected strategy
 #3 full path of the destination folder of the grounding process
 
###deployment executable (deployment.bat or deployment.sh)###
Takes 3 arguments:
 #1 PROSECO process id
 #2 target host url for deployment
 #3 port to listen on
