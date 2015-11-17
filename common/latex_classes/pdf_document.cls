\ProvidesClass{pdf_document}

\LoadClass[titlepage]{article}

\usepackage{graphicx}
\usepackage{caption}
\usepackage{hyperref}
\usepackage{color}
\usepackage{titlepic}

\newcommand{\myTaxiService}[0]{\mbox{\emph{myTaxiService}}}
\newcommand{\setDepth}[1]{\setcounter{tocdepth}{#1}\setcounter{secnumdepth}{#1}}
\newcommand{\sectionBaseDir}[1]{./section_#1}
\newcommand{\sectionTexPath}[1]{\sectionBaseDir{#1}/section_#1.tex}
\newcommand{\inputSection}[1]{\newpage\input{\sectionTexPath{#1}}}
\newcommand{\itemBold}[1]{\item\textbf{#1:}}
\newcommand{\unbreakableBlock}[1]{
	\begin{minipage}{\linewidth}% Following stays together
		#1
	\end{minipage}
}
\newcommand{\unbreakableItem}[1]{\unbreakableBlock{\item #1}}
\newcommand{\showPercentImage}[3]{
	\begin{center}
		\unbreakableBlock{
			\begin{center}
				\includegraphics[width=#3\textwidth,keepaspectratio=true]{#1}
				\captionof{figure}{#2}
			\end{center}
		}
	\end{center}
}
\newcommand{\showImage}[2]{\showPercentImage{#1}{#2}{1}}
\setDepth{4}
\hypersetup{
	colorlinks=true, %set true if you want colored links
	linktoc=all,     %set to all if you want both sections and subsections linked
	linkcolor=black,
}
\author{Alberto Mario Pirovano \and Alessandro Vetere}      % Declares authors' names.
\date{\today}	% This command produces today's date.
\titlepic{\includegraphics[width=0.7\textwidth]{../common/pictures/logo_polimi.png}}
\newcommand{\titleToc}{\maketitle\newpage\tableofcontents\newpage}