##########################################################################
###### Plotting tools for P3J Report Generation 
###### 
###### Author: Christina Bohk, University of Rostock
##########################################################################

#Required packages: 
require("RColorBrewer");
COLOR_PALETTE <- "PuBuGn";
QUANTILES <- c(0,0.025,0.05,0.1,0.5,0.9,0.95,0.975,1);

# Quickly reads in a matrix (using scan), with given years and number of age groups.
# path - path of the CSV file
# years - number of predicted years
# ages - number of distinct age groups
readMatrix <- function(path, years, ages){
	matrix(scan(path, dec='.', sep=",", n=(years*ages)), nrow=ages, byrow=TRUE)[,1:(years)];
}

# Creates a polygon plot.
# myData - the data containing the quantiles
# time - the vector of times
polygonPlot <- function(myData, time){
	col.palette <- brewer.pal((floor(length(myData[,1])/2)), COLOR_PALETTE)
    Final.col.palette <- c(col.palette[(floor(length(myData[,1])/2)):1],rev(col.palette[(floor(length(myData[,1])/2)):1]))

	par(mfrow=c(1,1),mar=c(5,3,3,1))
	plot.new()
	plot.window(ylim=c(min(myData),(max(myData))),xlim=c(min(time),max(time)))
	axis(2,font.axis=1, cex.axis=1.1)
	axis(1,font.axis=1, cex.axis=1.1)
	
	box()
	title(xlab="", ylab="", main="", cex.main=1.1)

	for(i in 1:((length(myData[,1]))-1))
		polygon(c(time,rev(time)),c(myData[i,],rev(myData[i+1,])),col=Final.col.palette[i])
}


# Creates a panel of 3x3 density plots for certain offsets 
# myData - the data to be plotted
# years - a vector of years for labeling the plots
densityPlot <- function(myData, years){

	dens <- matrix(0, nr=length(myData[,1]), nc=10)
	dens[,1] <- myData[,1]

	#Select the data
	for(i in 0:8){
		dens[,i+2] <- myData[,(4 + 5*i)]
	} 
	
	#Create the plots
	par(mfrow=c(3,3))
	for(i in 2:10){
		jumpOffValue <- myData[1,1]
		d <- dens[,i]
		d.hist <- hist(d,plot=F)
		y <- d.hist$intensities
		hist(d,freq=F, cex.axis=1.1, cex.main=1.1, main=years[i-1], xlab="", ylab="", xlim=c(min(dens),max(dens)), xaxp=c(min(dens),max(dens),4), yaxt="n", col="darkblue")
		MesoMax <- c((max(y)-min(y))*0.25,max(y))	
		axis(2, at=MesoMax, labels=formatC(MesoMax, format="f", digits=8)) 
		abline(v=jumpOffValue, col="red")
	}
}

# Creates a pyramid plot.
# quantilesMales - quantiles for males 
# quantilesFemales - quantiles for females 
# originalMale - original data for males
# originalFemale - original data for females
# colOrigMales - color for plotting original data for males 
# colOrigFemales - color for plotting original data for females
populationPyramidQuantilePlot <- function(quantilesMales, quantilesFemales, originalMale, originalFemale, colOrigMales, colOrigFemales) {

	PopAgeQuantile <- rbind(quantilesMales,quantilesFemales)
	JumpOff <- rbind(originalMale, originalFemale)
	maxNum <- max(PopAgeQuantile)
	if(max(JumpOff) > max(PopAgeQuantile)){
		maxNum <- max(JumpOff)
	}
	numAges <- length(quantilesMales[1,])

	plot.new()
	plot.window(xlim=c(-maxNum,maxNum), ylim=c(-8,numAges))

	range <- ceiling(max(PopAgeQuantile,digits=0));
	if(range %% 2 > 0)
		range <- range+1;
	ticks <- seq(-range,range,range/2);

	age <- seq(0,(numAges-1),1)
	axis(1, at=ticks, labels=abs(ticks), pos=0, font=1,cex.axis=1.1)
	axis(2, yaxp=c(formatC(floor(min(age)),digits=0),formatC(floor(max(age)),digits=0) ,20),font=1,cex.axis=1.1)
	title(xlab="", ylab="age", main="", cex.main=1.1)

	col.palette <- brewer.pal(floor(length(quantilesMales[,1])/2), COLOR_PALETTE)
	Final.palette <- c(rev(col.palette),col.palette)
	Final.palette[length(quantilesMales[,1])] <- c("white")

	#Males
	lines(-quantilesMales[1,], age, col="black", lty=2, lwd=2)
	lines(-quantilesMales[9,], age, col="black", lty=2, lwd=2)
	lines(-quantilesMales[5,], age, col="black", lty=1, lwd=2)

	#Females
	lines(quantilesFemales[1,], age, col="black", lty=2, lwd=2)
	lines(quantilesFemales[9,], age, col="black", lty=2, lwd=2)
	lines(quantilesFemales[5,], age, col="black", lty=1, lwd=2)

	if(sum(originalMale) > 0 & sum(originalFemale > 0)){
		lines(-originalMale[1,],age, lwd=2, col=colOrigMales)
		lines(originalFemale[1,],age, lwd=2, col=colOrigFemales)
	}

	lines((rep(0,numAges)), age)
}
