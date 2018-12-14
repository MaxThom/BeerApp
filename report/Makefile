SRCS = $(wildcard *.tex) $(wildcard *.bib)

report.pdf:	$(SRCS)
	pdflatex report
	#bibtex report
	pdflatex report
	# Third run sometimes needed for correct references if bibtex is used
	#pdflatex report

.PHONY:	pdf
pdf:	report.pdf
