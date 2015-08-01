library("RCurl")
library("ggplot2")

# Print with fractional seconds
op <- options(digits.secs = 3) 

df.sensors <- read.csv(text=getURL("http://localhost:8080/sensors/list"), header=FALSE, col.names=c("sensor"))
df.properties <- read.csv(text=getURL("http://localhost:8080/properties/list"), header=FALSE, col.names=c("property"))
df.features <- read.csv(text=getURL("http://localhost:8080/features/list"), header=FALSE, col.names=c("feature"))
df.datasets <- read.csv(text=getURL("http://localhost:8080/datasets/list"), header=FALSE, col.names=c("dataset"))

sensor <- "http://example.org#aThermometer"
property <- "http://example.org#temperature"
feature <- "http://example.org#air"
from <- "2015-05-18T00:00:00.000+03:00"
to <- "2015-05-18T00:02:00.000+03:00"
ylab <- "Temperature [C]"

sensor <- "http://example.org#ca"
property <- "http://sweet.jpl.nasa.gov/2.3/propMass.owl#Density"
feature <- "http://sweet.jpl.nasa.gov/2.3/matrCompound.owl#CarbonDioxide"
from <- "2015-01-07T00:15:00.000+06:00"
to <- "2015-01-07T00:20:00.000+06:00"
ylab <- "CO2 [mmol m-3]"

url <- paste0("http://localhost:8080/observations/sensor/list?", "sensor=", curlEscape(sensor), "&property=", curlEscape(property), "&feature=", curlEscape(feature), "&from=", curlEscape(from), "&to=", curlEscape(to))
df.observations <- read.csv(text=getURL(url, httpheader=c(Accept="text/csv")), header=TRUE, sep=",")
# Correction because strptime %z expects +0300 while ISO is +03:00
df.observations$time <- strptime(gsub("([+-]\\d\\d)(:)", "\\1", df.observations$time), "%Y-%m-%dT%H:%M:%OS%z", tz="UTC") # Canonicalize to UTC
ggplot(data=df.observations, aes(time, value)) + geom_line() + xlab("Time") + ylab(ylab)