\ProvidesClass{pdf_document}
%
\LoadClass[titlepage]{article}
%
\usepackage{graphicx}
\usepackage{caption}
\usepackage{hyperref}
\usepackage{titlepic}
\usepackage{listings}
\usepackage{color}
\usepackage{tabulary}
\usepackage{array}
%
\definecolor{dkgreen}{rgb}{0,0.6,0}
\definecolor{gray}{rgb}{0.5,0.5,0.5}
\definecolor{mauve}{rgb}{0.58,0,0.82}
%
\newcolumntype{X}[1]{>{\raggedright\let\newline\\\arraybackslash\hspace{0pt}}m{#1}}	\newcolumntype{Y}[1]{>{\centering\let\newline\\\arraybackslash\hspace{0pt}}m{#1}}
\newcolumntype{Z}[1]{>{\raggedleft\let\newline\\\arraybackslash\hspace{0pt}}m{#1}}
%
\newcommand{\versionHistoryTab}[1]{%
	\begin{tabulary}{\linewidth\tymin=70pt}{Y{3cm}|Y{3cm}|X{4.75cm}}
		\textbf{\textit{Version Number}} & \textbf{\textit{Release Date}} & \textbf{\textit{Changelog}} \\
		\hline
		#1
	\end{tabulary}%
}
%
\newcommand{\paragraphnewline}[1]{\paragraph{#1}\mbox{}\\}
%
\newcommand{\configureJava}{%
	\lstset{frame=tb,%
		language=Java,
		aboveskip=3mm,
		belowskip=3mm,
		showstringspaces=false,
		columns=flexible,
		basicstyle={\small\ttfamily},
		numbers=none,
		numberstyle=\tiny\color{gray},
		keywordstyle=\color{blue},
		commentstyle=\color{dkgreen},
		stringstyle=\color{mauve},
		breaklines=true,
		breakatwhitespace=true,
		tabsize=3%
	}%
}
\newcommand{\enableCodeLineNumbers}{\lstset{numbers=left}}
\newcommand{\disableCodeLineNumbers}{\lstset{numbers=none}}
%
\newcommand{\myTaxiService}[0]{\mbox{\emph{myTaxiService}}}
\newcommand{\setDepth}[1]{\setcounter{tocdepth}{#1}\setcounter{secnumdepth}{#1}}
\newcommand{\sectionBaseDir}[1]{./section_#1}
\newcommand{\sectionTexPath}[1]{\sectionBaseDir{#1}/section_#1.tex}
\newcommand{\inputSection}[1]{\newpage\input{\sectionTexPath{#1}}}
\newcommand{\itemBold}[1]{\item\textbf{#1:}}
\newcommand{\unbreakableBlock}[1]{%
	\begin{minipage}{\linewidth}% Following stays together
		#1
		\vspace{0.5em}
	\end{minipage}
}
\newcommand{\unbreakableItem}[1]{\unbreakableBlock{\item #1}}
\newcommand{\showPercentImage}[3]{%
	\begin{center}
		\unbreakableBlock{
			\begin{center}
				\includegraphics[width=#3\textwidth,keepaspectratio=true]{#1}
				\captionof{figure}{#2}
			\end{center}
		}
	\end{center}%
}
%
\newcommand{\showImage}[2]{\showPercentImage{#1}{#2}{1}}
\setDepth{4}
\hypersetup{%
	colorlinks=true, %set true if you want colored links
	linktoc=all,     %set to all if you want both sections and subsections linked
	linkcolor=black,
	urlcolor=blue%
}
\author{Alberto Mario Pirovano \and Alessandro Vetere}      % Declares authors' names.
\date{\today}	% This command produces today's date.
\titlepic{\includegraphics[width=0.7\textwidth]{../common/pictures/logo_polimi.png}}
\newcommand{\titleSubtitle}[2]{\title{#1 \\ \vspace{1cm} \large{#2}}}
\newcommand{\titleToc}{\maketitle\newpage\tableofcontents}