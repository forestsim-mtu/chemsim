#!/usr/local/bin/rscript

library(matrixStats)

options(scipen=5)

EXPERIMENTAL <- 'experiment/experimental.csv'

OUTPUT_DIR <- 'data/plots'
OUTPUT_PATH <- 'data/plots/%s.png'

getExperimental <- function(label) {
	# Load the data
	df <- read.csv(EXPERIMENTAL, skip = 1, header = T)
	
	# Return if the data is not present
	if (!(label %in% colnames(df))) {
		return(NULL)
	}
	
	# Extract only the relevent data
	data <- cbind(df['Min'], df[label])
	data <- na.omit(data)
	return(data)
}

load <- function(path) {
	data <- list()
	ndx <- 0
	for (file in list.files(path)) {
		# Prepare the path
		file = paste(path, file, sep='/')
		
		# Load the data
		headers = read.csv(file, skip = 1, header = F, nrows = 1, as.is = T)
		df = read.csv(file, skip = 2, header = F)
		colnames(df) = headers
		
		# Drop any empty (i.e., all NA) columns
		empty <- sapply(df, function(x) all(is.na(x)))
		df <- df[!empty]
		
		# Append to list
		ndx <- ndx + 1
		data[[ndx]] <- df
	}
	return(data)
}

process <- function(file, unit) {
	raw <- load(file)
	data <- list()
				
	# Find the largest number of rows so we can pad the raw data as needed
	max <- 0
	for (set in raw) {
		if (nrow(set) > max) {
			max <- nrow(set)
		}
	}
	
	# Append zeroed rows to the raw data if need be
	for (ndx in seq(1, length(raw), 1)) {
		rows <- nrow(raw[[ndx]])
		if (rows < max) {
			working <- raw[[ndx]]
			zeros <- matrix(0, nrow = max - rows, ncol = length(working))
			colnames(zeros) = names(working)
			raw[[ndx]] <- rbind(working, zeros)
		}
	}
	
	# Extract the data for each compound
	for (compound in colnames(raw[[1]])) {
		data[[compound]] <- matrix(, nrow = nrow(raw[[1]]), ncol = length(raw))
		for (ndx in 1:length(raw)) {
			col <- matrix(unlist(raw[[ndx]][compound]), ncol = 1, byrow = TRUE)
			data[[compound]][, ndx] <- col
		}	
	}
	
	# Plot the data 
	for (compound in colnames(raw[[1]])) {
		analysis(data[[compound]], compound, unit)
	}
}

analysis <- function(data, label, unit) {
	# If we are working with mols, covert to mM and load the experimetnal data
	experimental <- NULL;
	if (unit == 'Mols') {
		data <- (data * 1000) / 1.8
		unit <- 'mM'
		experimental <- getExperimental(label)
	}
		
	if (!is.null(experimental)) {
		plotExperimental(data, experimental, label, unit)
	} else {

		# Find the stats
		min <- rowMins(data)
		mean <- rowMeans(data)
		max <- rowMaxs(data)
		
		# Plot the data
		file = sprintf(OUTPUT_PATH, label)
		
		# Replace the star with a plus on Windows
		if (Sys.info()[['sysname']] == 'Windows') {
			file = gsub("\\*", "+", file)
		}
		
		png(file = file, width = 1280, height = 1040)
		par(mar = c(5, 5, 5, 5))
		plot(mean, type = 'l', xlab = 'Timestep, min', ylab = sprintf('%s, %s', label, unit),
				lwd = 2, cex.lab=1.5, cex.axis=1.5, cex.main=1.5, cex.sub=1.5)
		box(lwd=2)
		
#		lines(min, type='l', col='blue')
#		lines(max, type='l', col='red')
#		legend("right", legend = c("Mean", "Min", "Max"), col = c("black", "blue", "red"), lty=1, cex=0.8)

		dev.off()
	}
}

plotExperimental <- function(data, experimental, label, unit) {
	# Find the stats
	min <- rowMins(data)
	mean <- rowMeans(data)
	max <- rowMaxs(data)
	
	# Extract the points
	x <- as.list(experimental[,'Min'])
	y <- as.list(experimental[, label])	
	
	# Check to see if we need to adjust the x/y axis
	ylim <- c(min(unlist(y[which.min(y)]), unlist(mean[which.min(mean)]) ), 
			  max(unlist(y[which.max(y)]), unlist(mean[which.max(mean)])))
	xlim <- c(0, max(unlist(x[which.max(x)]), length(mean)))
	
	# Note the location of the legend
	loc = "bottomleft"
	if (mean[1] == 0) {
		loc = "bottomright"
	}
	
	# Plot the data
	file = sprintf(OUTPUT_PATH, label)
	png(file = file, width = 1280, height = 1040)
	par(mar = c(5, 5, 5, 5))
	plot(mean, type = 'l', xlab = 'Timestep, min', ylab = sprintf('%s, %s', label, unit), ylim = ylim, xlim = xlim,
			lwd = 2, cex.lab=1.5, cex.axis=1.5, cex.main=1.5, cex.sub=1.5)
	box(lwd=2)
	
#	lines(min, type='l', col='blue')
#	lines(max, type='l', col='red')
	
	# Add the experimetal points	
	points(x, y, pch=16, cex = 2, col="red")
	legend(loc, legend = c("Experimental", "Simulation"), box.col = "white", inset = 0.1, cex=1.5,
			col = c("red", "black"), lty=c(NA, 1), lwd=c(NA, 2), pch = c(16, NA))
	dev.off()
}


dir.create(OUTPUT_DIR, showWarnings = FALSE)
#process('../data/simple/molecules', 'Molecules')
process('data/mols', 'Mols')
