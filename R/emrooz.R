library(RCurl)
library(ggplot2)
library(grid)
library(dplyr)

host <- "http://localhost:8080"

# Print with fractional seconds
op <- options(digits.secs = 3) 

df.sensors <- read.csv(text=getURL(paste0(host, "/sensors/list")), header=FALSE, col.names=c("sensor"))
df.properties <- read.csv(text=getURL(paste0(host, "/properties/list")), header=FALSE, col.names=c("property"))
df.features <- read.csv(text=getURL(paste0(host, "/features/list")), header=FALSE, col.names=c("feature"))
df.datasets <- read.csv(text=getURL(paste0(host, "/datasets/list")), header=FALSE, col.names=c("dataset"))

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

url <- paste0(host, "/observations/sensor/list?", "sensor=", curlEscape(sensor), "&property=", curlEscape(property), "&feature=", curlEscape(feature), "&from=", curlEscape(from), "&to=", curlEscape(to))
df.observations <- read.csv(text=getURL(url, httpheader=c(Accept="text/csv")), header=TRUE, sep=",")
# Correction because strptime %z expects +0300 while ISO is +03:00
df.observations$time <- strptime(gsub("([+-]\\d\\d)(:)", "\\1", df.observations$time), "%Y-%m-%dT%H:%M:%OS%z", tz="UTC") # Canonicalize to UTC
ggplot(data=df.observations, aes(time, value)) + geom_line() + xlab("Time") + ylab(ylab)

dataset <- "http://example.org#d1"
from <- "2015-01-07T00:15:00.000+06:00"
to <- "2015-01-07T00:20:00.000+06:00"
url <- paste0(host, "/observations/dataset/list?", "dataset=", curlEscape(dataset), "&from=", curlEscape(from), "&to=", curlEscape(to))
df.observations <- read.csv(text=getURL(url, httpheader=c(Accept="text/csv")), header=TRUE, sep=",")
df.observations$time <- strptime(gsub("([+-]\\d\\d)(:)", "\\1", df.observations$time), "%Y-%m-%dT%H:%M:%OS%z", tz="UTC") # Canonicalize to UTC
plot1 <- df.observations %>%
  select(time, carbonDioxideMoleFraction) %>%
  na.omit() %>%
  ggplot() +
  geom_line(aes(x=time, y=carbonDioxideMoleFraction), size=0.5) +
  ylab("CO2 [ppm]") +
  theme(axis.title.x=element_blank())
plot2 <- df.observations  %>%
  select(time, methaneMoleFraction) %>%
  na.omit() %>%
  ggplot() +
  geom_line(aes(x=time, y=methaneMoleFraction), size=0.5) +
  labs(x="Time [UTC]", y="CH4 [ppm]")
grid.newpage()
grid.draw(rbind(ggplotGrob(plot1), ggplotGrob(plot2), size="last"))