#!/usr/local/bin/bash

# using map require bash 4
# brew update && brew install bash
# ref: http://tldrdevnotes.com/bash-upgrade-3-4-macos

PRIMARY='\033[0;36m'
ACCENT='\033[0;33m'
WARN='\033[1;31m'
NC='\033[0m'

accentLog(){
    printf "${ACCENT}======================================\n$1\n======================================${NC}\n"
}

warnLog(){
    printf "${WARN}$1${NC}\n"
}

primaryLog(){
    printf "${PRIMARY}======================================\n$1\n======================================${NC}\n"
}

declare -A libVersionMap

exclude(){
    if [ $1 == "extension-flac" ]; then
        return 0
    else
        return 1
    fi
}

run(){
    text=$(../gradlew projects | grep -e '--- Project' | awk -v v="'" -F: '{gsub(v,"",$2);print $2}')
    modules=(${text// / })
    for i in "${!modules[@]}"
    do
        module=${modules[i]}

        if $(exclude ${module}) ; then
            warnLog "Ignore $module"
        else
            primaryLog "Analyzing $module"
            analyzeModule ${module}
        fi
    done
    report
}

analyzeModule(){
    text=$(../gradlew -q dependencies $1:dependencies --configuration implementation | grep -e '--- ' | awk '$2 !~ /project/ {print $2}')
    libs=(${text// / })
    if [ ${#libs[@]} == 0 ]; then
        warnLog "no dependency"
    else
        for i in "${!libs[@]}"
        do
            lib=${libs[i]}
            name=$(echo ${lib} | rev | cut -d":" -f2-  | rev)
            version=$(echo ${lib} | rev | cut -d":" -f1  | rev)
            echo ${name} ------- ${version}
            old=${libVersionMap[$name]}
            libVersionMap[${name}]="${old} ${version}"
        done
    fi
    printf "\n"
}

report(){
    accentLog "Report"
    for key in ${!libVersionMap[@]}
    do
        value=${libVersionMap[$key]}
        versions=(${value// / })
        for i in "${!versions[@]}"
        do
            version=${versions[i]}
            echo ${key}: ${version}
        done
    done
}

run