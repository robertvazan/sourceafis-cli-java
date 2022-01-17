# This script generates and updates project configuration files.

# We are assuming that project-config is available in sibling directory.
# Checkout from https://github.com/robertvazan/project-config
import os.path
import sys
sys.path.append(os.path.normpath(os.path.join(__file__, '../../../project-config/src')))

from java import *

project_script_path = __file__
repository_name = lambda: 'sourceafis-cli-java'
pretty_name = lambda: 'SourceAFIS CLI for Java'
pom_subgroup = lambda: 'sourceafis'
pom_artifact = lambda: 'sourceafis-cli'
pom_name = lambda: 'SourceAFIS CLI'
pom_description = lambda: 'Command-line interface for SourceAFIS.'
inception_year = lambda: 2021
homepage = lambda: website() + 'cli'
jdk_version = lambda: 11
main_class_name = lambda: 'Main'
project_status = lambda: experimental_status()
md_description = lambda: '''\
    SourceAFIS CLI for Java is a command-line interface to [SourceAFIS for Java](https://sourceafis.machinezoo.com/java).
    At the moment, it can benchmark algorithm accuracy, template footprint, and implementation speed.
    It also includes tools that aid in development of SourceAFIS for Java.
    Read more on [SourceAFIS CLI](https://sourceafis.machinezoo.com/cli) page.
'''

def dependencies():
    use('com.machinezoo.sourceafis:sourceafis:3.15.0')
    use_slf4j()
    use_commons_lang()
    use_streamex()
    use('ch.qos.logback:logback-classic:1.2.3')
    # Used to measure memory footprint of SourceAFIS templates.
    # Causes warnings. Needs replacement.
    use('org.openjdk.jol:jol-core:0.16')

def documentation_links():
    yield 'SourceAFIS CLI', 'https://sourceafis.machinezoo.com/cli'
    yield 'SourceAFIS for Java', 'https://sourceafis.machinezoo.com/java'
    yield 'SourceAFIS overview', 'https://sourceafis.machinezoo.com/'

generate(globals())
