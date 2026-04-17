import { LandingNavbar } from '../components/landing/Navbar'
import { HeroSection } from '../components/landing/HeroSection'
import { FeaturesSection } from '../components/landing/FeaturesSection'
import { HowItWorksSection } from '../components/landing/HowItWorksSection'
import { StatsSection } from '../components/landing/StatsSection'
import { CTASection } from '../components/landing/CTASection'
import { Footer } from '../components/landing/Footer'

export default function LandingPage() {
  return (
    <div className="relative bg-paper">
      <LandingNavbar />
      <HeroSection />
      <FeaturesSection />
      <HowItWorksSection />
      <StatsSection />
      <CTASection />
      <Footer />
    </div>
  )
}
