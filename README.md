[![Build Status](https://travis-ci.com/fmohr/PROSECO.svg?branch=development)](https://travis-ci.com/fmohr/PROSECO)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fmohr.PROSECO&metric=alert_status)](https://sonarcloud.io/dashboard?id=fmohr.PROSECO)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=fmohr.PROSECO&metric=coverage)](https://sonarcloud.io/dashboard?id=fmohr.PROSECO)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=fmohr.PROSECO&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=fmohr.PROSECO)
# PROSECO
The PROtotype-based SErvice COmposition (PROSECO) Framework is a free and open-source Java tool 
for automated service composition. PROSECO uses prototypes as basis of the services, and builds 
up on these prototypes by gathering user input. Services could be any type of server application.
Current examples include automated machine learning and cloud gaming.

## Building and Running PROSECO
In order to build PROSECO:
* clone or download the [source code](https://github.com/fmohr/PROSECO): `git clone https://github.com/fmohr/PROSECO.git`
* in the root folder run `gradlew build -x test`
* PROSECO-*.jar should be generated in the root directory run it with: `java -jar PROSECO-*.jar`
* You can then access the web application via `http://localhost:8080`

PROSECO needs prototypes to operate:
* AutoML Prototype can be set up using instructions at: https://git.cs.upb.de/SFB901-Testbed/automl-prototype
* Cloud Gaming Prototype can be set up using instructions at: https://github.com/kadirayk/game

## Documentation
A detailed documentation is available at: https://fmohr.github.io/PROSECO/

## Cite PROSECO
We would appreciate your citation, if you use PROSECO in a publication:

Felix Mohr, Marcel Wever, and Eyke Hüllermeier. "On-the-Fly Service Construction with Prototypes." 2018 IEEE International Conference on Services Computing (SCC). IEEE, 2018.

```
@inproceedings{DBLP:conf/IEEEscc/MohrWH18,
  author    = {Felix Mohr and
               Marcel Wever and
               Eyke H{\"{u}}llermeier},
  title     = {On-the-Fly Service Construction with Prototypes},
  booktitle = {2018 {IEEE} International Conference on Services Computing, {SCC}
               2018, San Francisco, CA, USA, July 2-7, 2018},
  pages     = {225--232},
  year      = {2018},
  crossref  = {DBLP:conf/IEEEscc/2018},
  url       = {https://doi.org/10.1109/SCC.2018.00036},
  doi       = {10.1109/SCC.2018.00036},
  timestamp = {Sat, 15 Sep 2018 15:28:10 +0200},
  biburl    = {https://dblp.org/rec/bib/conf/IEEEscc/MohrWH18},
  bibsource = {dblp computer science bibliography, https://dblp.org}
}
```
