# This script generates and updates project configuration files.

# Run this script with rvscaffold in PYTHONPATH
import rvscaffold as scaffold

class Project(scaffold.Java):
    def script_path_text(self): return __file__
    def repository_name(self): return 'sourceafis-cli-java'
    def pretty_name(self): return 'SourceAFIS CLI for Java'
    def pom_name(self): return 'SourceAFIS CLI'
    def pom_description(self): return 'Command-line interface for SourceAFIS.'
    def inception_year(self): return 2021
    def homepage(self): return self.website() + 'cli'
    def jdk_version(self): return 17
    def main_class_name(self): return 'Main'
    def stagean_annotations(self): return True
    def project_status(self): return self.stable_status()
    def md_description(self): return '''\
        SourceAFIS CLI for Java is a command-line interface to [SourceAFIS for Java](https://sourceafis.machinezoo.com/java).
        At the moment, it can benchmark algorithm accuracy, template footprint, and implementation speed.
        It also includes tools that aid in development of SourceAFIS for Java.
        Read more on [SourceAFIS CLI](https://sourceafis.machinezoo.com/cli) page.
    '''
    
    def dependencies(self):
        yield from super().dependencies()
        yield self.use('com.machinezoo.sourceafis:sourceafis:3.18.1')
        yield self.use_commons_lang()
        yield self.use_commons_io()
        yield self.use_streamex()
        # Used to measure memory footprint of SourceAFIS templates.
        # Causes warnings. Needs replacement.
        yield self.use('org.openjdk.jol:jol-core:0.16')
    
    def documentation_links(self):
        yield 'SourceAFIS CLI', 'https://sourceafis.machinezoo.com/cli'
        yield 'SourceAFIS for Java', 'https://sourceafis.machinezoo.com/java'
        yield 'SourceAFIS overview', 'https://sourceafis.machinezoo.com/'

Project().generate()
