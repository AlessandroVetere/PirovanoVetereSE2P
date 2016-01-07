% Copyright 2004 by Till Tantau <tantau@users.sourceforge.net>.
%
% In principle, this file can be redistributed and/or modified under
% the terms of the GNU Public License, version 2.
%
% However, this file is supposed to be a template to be modified
% for your own needs. For this reason, if you use this file as a
% template and not specifically distribute it as part of a another
% package/program, I grant the extra permission to freely copy and
% modify this file as you see fit and even to delete this copyright
% notice. 

\ProvidesClass{pdf_presentation}

\LoadClass{beamer}

\usepackage{listings}
\usepackage{color}
\usepackage{graphicx}
\usepackage{caption}

\definecolor{dkgreen}{rgb}{0,0.6,0}
\definecolor{gray}{rgb}{0.5,0.5,0.5}
\definecolor{mauve}{rgb}{0.58,0,0.82}

\newcommand{\configureJava}{
	\lstset{frame=tb,
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
		tabsize=3
	}
}

% There are many different themes available for Beamer. A comprehensive
% list with examples is given here:
% http://deic.uab.es/~iblanes/beamer_gallery/index_by_theme.html
% You can uncomment the themes below if you would like to use a different
% one:
%\usetheme{AnnArbor}
%\usetheme{Antibes}
%\usetheme{Bergen}
%\usetheme{Berkeley}
%\usetheme{Berlin}
%\usetheme{Boadilla}
%\usetheme{boxes}
%\usetheme{CambridgeUS}
%\usetheme{Copenhagen}
%\usetheme{Darmstadt}
%\usetheme{default}
%\usetheme{Frankfurt}
%\usetheme{Goettingen}
%\usetheme{Hannover}
%\usetheme{Ilmenau}
%\usetheme{JuanLesPins}
%\usetheme{Luebeck}
\usetheme{Madrid}
%\usetheme{Malmoe}
%\usetheme{Marburg}
%\usetheme{Montpellier}
%\usetheme{PaloAlto}
%\usetheme{Pittsburgh}
%\usetheme{Rochester}
%\usetheme{Singapore}
%\usetheme{Szeged}
%\usetheme{Warsaw}


% A subtitle is optional and this may be deleted
\subtitle{Software Engineering 2 Project}


\author{A.~Pirovano \and A.~Vetere}
%\author{F.~Author\inst{1} \and S.~Another\inst{2}}
% - Give the names in the same order as the appear in the paper.
% - Use the \inst{?} command only if the authors have different
%   affiliation.

\institute[PoliMi]
{Politecnico di Milano}
%\institute[Universities of Somewhere and Elsewhere] % (optional, but mostly needed)
%{
%	\inst{1}%
%	Department of Computer Science\\
%	University of Somewhere
%	\and
%	\inst{2}%
%	Department of Theoretical Philosophy\\
%	University of Elsewhere}
% - Use the \inst command only if there are several affiliations.
% - Keep it simple, no one is interested in your street address.

\date{\today}
%\date{Conference Name, 2013}
% - Either use conference name or its abbreviation.
% - Not really informative to the audience, more for people (including
%   yourself) who are reading the slides online

% If you have a file called "university-logo-filename.xxx", where xxx
% is a graphic format that can be processed by latex or pdflatex,
% resp., then you can add a logo as follows:

% \pgfdeclareimage[height=0.5cm]{university-logo}{university-logo-filename}
% \logo{\pgfuseimage{university-logo}}
\pgfdeclareimage[height=0.75cm]{university-logo}{../common/pictures/logo_polimi.png}
\logo{\pgfuseimage{university-logo}}

% Delete this, if you do not want the table of contents to pop up at
% the beginning of each subsection:
%\AtBeginSubsection[]
%{
%	\begin{frame}<beamer>{Outline}
%		\tableofcontents[currentsection,currentsubsection]
%	\end{frame}
%}

\AtBeginSection[]
{
	\begin{frame}<beamer>
		\frametitle{Outline - \insertsection}
		\tableofcontents[currentsection]
	\end{frame}
}


\newcommand{\titleToc}{
		\begin{frame}
			\titlepage
		\end{frame}
		
		\begin{frame}{Outline}
			\tableofcontents
			% You might wish to add the option [pausesections]
		\end{frame}
}

\newcommand{\myTaxiService}[0]{\mbox{\emph{myTaxiService}}}
\newcommand{\unbreakableBlock}[1]{
	\begin{minipage}{\linewidth}% Following stays together
		#1
	\end{minipage}
}
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
\newcommand{\itemBold}[1]{\item\textbf{#1:}}